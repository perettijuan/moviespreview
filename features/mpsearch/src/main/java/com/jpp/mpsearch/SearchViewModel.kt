package com.jpp.mpsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.CoroutineExecutor
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.paging.MPPagingDataSourceFactory
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mpsearch.SearchInteractor.SearchEvent.NotConnectedToNetwork
import com.jpp.mpsearch.SearchInteractor.SearchEvent.UnknownError
import com.jpp.mpsearch.SearchViewState.*
import javax.inject.Inject

/**
 * [MPScopedViewModel] implementation to serve the search feature of the application.
 * It uses the Paging Library in order to provide infinite scroll.
 */
class SearchViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                          private val searchInteractor: SearchInteractor,
                                          private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {


    private var targetImageSize: Int = -1
    private val _viewStates by lazy { MediatorLiveData<HandledViewState<SearchViewState>>() }
    private var retryFunc: (() -> Unit)? = null

    init {
        _viewStates.addSource(searchInteractor.searchEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(ShowNotConnected)
                is UnknownError -> _viewStates.value = of(ShowError)
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
        _viewStates.value = of(ShowSearchView)
    }

    /**
     * Perform the actual onSearch of the provided [query].
     */
    fun onSearch(query: String) {
        retryFunc = { pushLoadingAndPerformSearch(query) }
        pushLoadingAndPerformSearch(query)
    }

    /**
     * Clears the view current search state.
     */
    fun onClearSearch() {
        _viewStates.value = of(ShowSearchView)
    }

    fun onRetry() {
        retryFunc?.invoke()
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<SearchViewState>> = _viewStates

    /**
     * Pushes the Loading view state into the view layer and creates the [PagedList]
     * of [SearchResultItem] that will be rendered by the view layer.
     */
    private fun pushLoadingAndPerformSearch(query: String) {
        with(_viewStates) {
            value = of(ShowSearching)
            addSource(createPagedListForSearch(query)) { pagedList -> if (pagedList.size > 0) value = of(ShowSearchResults(pagedList)) }
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
                    false -> if (page == 1) _viewStates.postValue(of(ShowEmptySearch(query)))
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