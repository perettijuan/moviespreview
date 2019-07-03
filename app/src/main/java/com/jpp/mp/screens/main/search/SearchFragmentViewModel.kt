package com.jpp.mp.screens.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mpdomain.SearchResult
import com.jpp.mp.paging.MPPagingDataSourceFactory
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mpdomain.usecase.search.ConfigSearchResultUseCase
import com.jpp.mpdomain.usecase.search.SearchUseCase
import com.jpp.mpdomain.usecase.search.SearchUseCaseResult
import java.util.concurrent.Executor
import javax.inject.Inject

/**
 * [ViewModel] implementation to serve the SearchFragment.
 *
 * Output: exposes a LiveData of [SearchViewState] that is updated with each new state that is
 * identified by the ViewModel.
 */
//TODO JPP cuando lo implementes en el modulo nuevo, acordate del filter
class SearchFragmentViewModel @Inject constructor(private val searchUseCase: SearchUseCase,
                                                  private val configSearchResultUseCase: ConfigSearchResultUseCase,
                                                  private val networkExecutor: Executor) : ViewModel() {

    private var targetImageSize: Int = -1
    private val viewState = MediatorLiveData<SearchViewState>()
    private val navigationEvents  by lazy { SingleLiveEvent<SearchViewNavigationEvent>() }
    private var retryFunc: (() -> Unit)? = null
    private var currentSearch = ""

    /**
     * Called on initialization to prepare the internal state of the ViewModel.
     * [imageSize] represents the size of the images that will be shown in the view
     * that is served by this ViewModel.
     */
    fun init(imageSize: Int) {
        targetImageSize = imageSize
    }

    /**
     * Exposes a stream that is updated with a new state ([SearchViewState])
     * every time a new state is identified.
     */
    fun viewState(): LiveData<SearchViewState> = viewState

    /**
     * Exposes the events that are triggered when a navigation event is detected.
     * We need a different LiveData here in order to avoid the problem of back navigation:
     * - The default LiveData object posts the last value every time a new observer starts observing.
     */
    fun navEvents(): LiveData<SearchViewNavigationEvent> = navigationEvents

    /**
     * Called when an item is selected in the list of search results.
     * A new state is posted in navEvents() in order to handle the event.
     */
    fun onSearchItemSelected(searchResultItem: SearchResultItem) {
        when (searchResultItem.icon) {
            is SearchResultTypeIcon.MovieType -> with(searchResultItem) {
                navigationEvents.value = SearchViewNavigationEvent.ToMovieDetails(movieId = id.toString(), movieImageUrl = imagePath, movieTitle = name)
            }
            is SearchResultTypeIcon.PersonType -> with(searchResultItem) {
                navigationEvents.value = SearchViewNavigationEvent.ToPerson(personId = id.toString(), personImageUrl = imagePath, personName = name)
            }
        }
    }

    /**
     * Performs the search of the provided [searchText] in the application. Posts [SearchViewState]
     * values when new states are identified:
     * - [SearchViewState.Searching] -> when a new search is triggered.
     * - [SearchViewState.DoneSearching] -> when the the initial search of the [searchText] is done.
     *   At this point, the ViewModel provides a [PagedList] that can be bind to a RecyclerView to
     *   perform infinite scrolling.
     */
    fun search(searchText: String) {
        if (currentSearch == searchText) {
            return
        }

        currentSearch = searchText
        viewState.value = SearchViewState.Searching
        createSearchPagedList(searchText).let {
            viewState.addSource(it) { pagedList ->
                if (pagedList.size > 0) {
                    viewState.value = SearchViewState.DoneSearching(pagedList)
                } else {
                    currentSearch = ""
                    retryFunc = { search(searchText) }
                }
            }
        }
    }

    /**
     * Clears the inner state of the ViewModel when the user clears the current
     * search.
     */
    fun clearSearch() {
        retryFunc = null
        currentSearch = ""
        viewState.value = SearchViewState.Idle
    }

    /**
     * Attempts to execute the last search that was executed. Typically called after an error
     * is detected.
     */
    fun retryLastSearch() {
        retryFunc?.invoke()
    }


    /**
     * This is the method that creates the actual [PagedList] that will be used to provide
     * infinite scrolling. It uses a [MPPagingDataSourceFactory] that is mapped to have the
     * desired model ([SearchResultItem]) in order to create the [PagedList].
     *
     * The steps to create the proper instance of [PagedList] are:
     * 1 - Create a [MPPagingDataSourceFactory] instance of type [SearchResult].
     * 2 - Map the created instance to a second DataSourceFactory of type [SearchResult], to
     *     execute the configurations of the images path of every result.
     * 3 - Map the instance created in (2) to a new DataSourceFactory that will map the
     *     [SearchResult] to a [SearchResultItem].
     */
    private fun createSearchPagedList(query: String): LiveData<PagedList<SearchResultItem>> {
        return MPPagingDataSourceFactory<SearchResult> { page, callback -> searchMoviePage(query, page, callback) } // (1) -
                .apply { retryFunc = { networkExecutor.execute { retryLast() } } } // (1.1) -> create a retry function.
                .map { configSearchResultUseCase.configure(targetImageSize, it) } // (2)
                .map { mapSearchResult(it) } // (3)
                .let {
                    // build the PagedList
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(3)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(networkExecutor)
                            .build()
                }
    }


    /**
     * This is the method that actually performs the search. This method is called every time that
     * the [PagedList] detects that a new page needs to be fetched.
     */
    private fun searchMoviePage(queryString: String, page: Int, callback: (List<SearchResult>, Int) -> Unit) {
        searchUseCase
                .search(queryString, page)
                .let { ucResult ->
                    when (ucResult) {
                        is SearchUseCaseResult.ErrorNoConnectivity -> {
                            viewState.postValue(if (page > 1) SearchViewState.ErrorNoConnectivityWithItems else SearchViewState.ErrorNoConnectivity)
                        }
                        is SearchUseCaseResult.ErrorUnknown -> {
                            viewState.postValue(if (page > 1) SearchViewState.ErrorUnknownWithItems else SearchViewState.ErrorUnknown)
                        }
                        is SearchUseCaseResult.Success -> {
                            with(ucResult.searchPage.results) {
                                when (page) {
                                    // Post empty value only when it is the first page
                                    1 -> if (size > 0) callback(this, page + 1) else viewState.postValue(SearchViewState.EmptySearch(queryString))
                                    else -> callback(this, page + 1)
                                }
                            }
                        }
                    }
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
        true -> SearchResultTypeIcon.MovieType
        else -> SearchResultTypeIcon.PersonType
    }
}