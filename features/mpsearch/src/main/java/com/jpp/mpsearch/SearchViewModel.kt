package com.jpp.mpsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.CoroutineExecutor
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.paging.MPPagingDataSourceFactory
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mpsearch.SearchInteractor.SearchEvent.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] used to support the search section of the application.
 * Produces different [SearchViewState] that represents the entire configuration of the screen at any
 * given moment.
 *
 * Since the UI is using the Android Paging Library, the VM needs a way to map the data retrieved from
 * the [SearchInteractor] to a [PagedList] that can be used by the library. That process is done
 * using the [MPPagingDataSourceFactory] that creates the DataSource and produces a [LiveData] object
 * that is combined with the [viewState] in order to properly map the data into a [SearchViewState].
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class SearchViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                          private val searchInteractor: SearchInteractor,
                                          private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewState = MediatorLiveData<HandledViewState<SearchViewState>>()
    val viewState: LiveData<HandledViewState<SearchViewState>> = _viewState

    private val _navEvents = SingleLiveEvent<SearchNavigationEvent>()
    val navEvents: LiveData<SearchNavigationEvent> get() = _navEvents

    private var targetImageSize: Int = -1
    private lateinit var searchQuery: String

    private var retryFunc = { postLoadingAndPerformSearch(searchQuery) }

    init {
        _viewState.addSource(searchInteractor.searchEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewState.value = of(SearchViewState.showNoConnectivityError(searchQuery, retryFunc))
                is UnknownError -> _viewState.value = of(SearchViewState.showUnknownError(searchQuery, retryFunc))
                is AppLanguageChanged -> refreshData()
            }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(imageSize: Int) {
        targetImageSize = imageSize
        when (val currentState = _viewState.value) {
            null -> _viewState.value = of(SearchViewState.showCleanState())
            else -> _viewState.value = of(currentState.peekContent())
        }
    }

    /**
     * Called when the user performs a search. The VM will verify the inner state
     * of the application and will perform a search of the provided [query]. Once
     * a result is obtained from the [searchInteractor] a new view state will be
     * posted to [viewState].
     */
    fun onSearch(query: String) {
        if (::searchQuery.isInitialized && query == searchQuery) {
            return
        }

        searchQuery = query
        postLoadingAndPerformSearch(query)
    }

    /**
     * Called when the user clears the state of the last search performed.
     * The VM clears the inner state and the view state, in order to allow
     * a new search to be done.
     */
    fun onClearSearch() {
        searchQuery = ""
        _viewState.value = of(SearchViewState.showCleanState())
    }

    /**
     * Called when an item is selected in the list of search results.
     * A new state is posted in [navEvents] in order to handle the event.
     */
    fun onItemSelected(item: SearchResultItem, positionInList: Int) {
        when (item.isMovieType()) {
            true -> _navEvents.value = SearchNavigationEvent.GoToMovieDetails(
                    movieId = item.id.toString(),
                    movieImageUrl = item.imagePath,
                    movieTitle = item.name,
                    positionInList = positionInList)
            false -> _navEvents.value = SearchNavigationEvent.GoToPerson(
                    personId = item.id.toString(),
                    personImageUrl = item.imagePath,
                    personName = item.name
            )
        }
    }


    /**
     * Pushes the Loading view state into the view layer and creates the [PagedList]
     * of [SearchResultItem] that will be rendered by the view layer.
     */
    private fun postLoadingAndPerformSearch(query: String) {
        with(_viewState) {
            value = of(SearchViewState.showSearching(query))
            addSource(createPagedListForSearch(query)) { pagedList -> if (pagedList.size > 0) value = of(SearchViewState.showSearchResult(query, pagedList)) }
        }
    }

    /**
     * Asks the interactor to flush any data that might be locally cached and re-fetch the
     * movie list for the current section being shown.
     */
    private fun refreshData() {
        launch {
            withContext(Dispatchers.Default) { searchInteractor.flushCurrentSearch() }
            postLoadingAndPerformSearch(searchQuery)
        }
    }

    /**
     * Creates the [LiveData] of [PagedList] that will be pushed to the view layer to render each onSearch result
     * as a [SearchResultItem].
     */
    private fun createPagedListForSearch(query: String): LiveData<PagedList<SearchResultItem>> {
        return MPPagingDataSourceFactory<SearchResult> { page, callback ->
            searchInteractor.performSearchForPage(query, page) { searchResultList ->
                when (searchResultList.isNotEmpty()) {
                    true -> callback(searchResultList.filter { it.isMovie() || it.isPerson() }.map { imagesPathInteractor.configureSearchResult(targetImageSize, it) })
                    false -> if (page == 1) _viewState.postValue(of(SearchViewState.showNoResults(query)))
                }
            }
        }.map { mapSearchResult(it) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(2)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(CoroutineExecutor(this, Dispatchers.Default))
                            .build()
                }
    }

    private fun mapSearchResult(searchResult: SearchResult) = with(searchResult) {
        SearchResultItem(
                id = id,
                imagePath = extractImagePathFromSearchResult(this),
                name = extractTitleFromSearchResult(this),
                icon = getIconForSearchResult(this)
        )
    }

    private fun extractImagePathFromSearchResult(domainSearchResult: SearchResult) = when (domainSearchResult.isMovie()) {
        true -> domainSearchResult.poster_path ?: "Unknown"
        else -> domainSearchResult.profile_path ?: "Unknown" // case: TV and MOVIE
    }

    private fun extractTitleFromSearchResult(domainSearchResult: SearchResult) = when (domainSearchResult.isMovie()) {
        true -> domainSearchResult.title ?: "Unknown"
        else -> domainSearchResult.name ?: "Unknown" // case: TV and PERSON
    }

    private fun getIconForSearchResult(domainSearchResult: SearchResult) = when (domainSearchResult.isMovie()) {
        true -> SearchResultTypeIcon.Movie
        else -> SearchResultTypeIcon.Person
    }
}