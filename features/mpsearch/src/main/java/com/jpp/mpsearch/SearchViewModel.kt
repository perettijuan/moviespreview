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
import javax.inject.Inject

/**
 * [MPScopedViewModel] implementation to serve the search feature of the application.
 * It uses the Paging Library in order to provide infinite scroll.
 */
class SearchViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                          private val searchInteractor: SearchInteractor,
                                          private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewStates by lazy { MediatorLiveData<HandledViewState<SearchViewState>>() }
    val viewStates: LiveData<HandledViewState<SearchViewState>> = _viewStates

    private val _navEvents by lazy { SingleLiveEvent<SearchNavigationEvent>() }
    val navEvents: LiveData<SearchNavigationEvent> get() = _navEvents

    private var retryFunc = { pushLoadingAndPerformSearch(searchQuery) }

    private var targetImageSize: Int = -1
    private lateinit var searchQuery: String

    init {
        _viewStates.addSource(searchInteractor.searchEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(SearchViewState.showNoConnectivityError(searchQuery, retryFunc))
                is UnknownError -> _viewStates.value = of(SearchViewState.showUnknownError(searchQuery, retryFunc))
                is AppLanguageChanged -> refreshData()
            }
        }
    }

    /**
     * Called on initialization to prepare the internal state of the ViewModel.
     * [imageSize] represents the size of the images that will be shown in the view
     * that is served by this ViewModel.
     */
    fun onInit(imageSize: Int) {
        targetImageSize = imageSize
        when (val currentState = _viewStates.value) {
            null -> _viewStates.value = of(SearchViewState.showCleanState())
            else -> _viewStates.value = of(currentState.peekContent())
        }
    }

    /**
     * Perform the actual onSearch of the provided [query].
     */
    fun onSearch(query: String) {
        if (::searchQuery.isInitialized && query == searchQuery) {
            return
        }

        searchQuery = query
        pushLoadingAndPerformSearch(query)
    }

    /**
     * Clears the view current search state.
     */
    fun onClearSearch() {
        searchQuery = ""
        _viewStates.value = of(SearchViewState.showCleanState())
    }

    /**
     * Called when an item is selected in the list of search results.
     * A new state is posted in navEvents() in order to handle the event.
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
    private fun pushLoadingAndPerformSearch(query: String) {
        with(_viewStates) {
            value = of(SearchViewState.showSearching(query))
            addSource(createPagedListForSearch(query)) { pagedList -> if (pagedList.size > 0) value = of(SearchViewState.showSearchResult(query, pagedList)) }
        }
    }

    /**
     * Refreshes any internal app state related to a search.
     */
    private fun refreshData() {
        searchInteractor.flushCurrentSearch()
        retryFunc.invoke()
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
                    false -> if (page == 1) _viewStates.postValue(of(SearchViewState.showNoResults(query)))
                }
            }
        }.map { mapSearchResult(it) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(2)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(CoroutineExecutor(this, dispatchers.default()))
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