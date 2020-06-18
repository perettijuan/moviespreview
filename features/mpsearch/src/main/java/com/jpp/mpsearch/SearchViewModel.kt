package com.jpp.mpsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.usecase.SearchUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [MPViewModel] used to support the search section of the application.
 * Produces different [SearchViewState] that represents the entire configuration of the screen at any
 * given moment.
 */
class SearchViewModel(
    private val searchUseCase: SearchUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : MPViewModel() {

    private val _viewState = MutableLiveData<SearchViewState>()
    internal val viewState: LiveData<SearchViewState> = _viewState

    private var currentPage: Int = 0
    private var maxPage: Int = 0
    private lateinit var searchQuery: String

    /**
     * The View (Fragment) should call this method to
     * indicate that it is ready to start rendering.
     */
    internal fun onInit() {
        updateCurrentDestination(Destination.MPSearch)

        when (val currentState = _viewState.value) {
            null -> _viewState.value = SearchViewState.showCleanState()
            else -> _viewState.value = currentState
        }
    }

    /**
     * Called when the user performs a search.
     */
    internal fun onSearch(query: String) {
        if (::searchQuery.isInitialized && query == searchQuery) {
            return
        }

        searchQuery = query
        _viewState.value = _viewState.value?.showSearching(query)
        performSearch(query, FIRST_PAGE)
    }

    /**
     * Called when a new page in the search is requested.
     */
    internal fun onNextPageRequested() {
        val nextPage = currentPage + 1
        performSearch(searchQuery, nextPage)
    }

    /**
     * Called when the user clears the state of the last search performed.
     */
    internal fun onClearSearch() {
        searchQuery = ""
        _viewState.value = SearchViewState.showCleanState()
    }

    /**
     * Called when an item is selected in the list of search results.
     */
    internal fun onItemSelected(item: SearchResultItem) {
        when (item.isMovieType()) {
            true -> navigateTo(
                Destination.MPMovieDetails(
                    movieId = item.id.toString(),
                    movieImageUrl = item.imagePath,
                    movieTitle = item.name
                )
            )
            false -> navigateTo(
                Destination.MPPerson(
                    personId = item.id.toString(),
                    personImageUrl = item.imagePath,
                    personName = item.name
                )
            )
        }
    }

    /**
     * Pushes the Loading view state into the view layer and creates the [PagedList]
     * of [SearchResultItem] that will be rendered by the view layer.
     */
    private fun performSearch(query: String, page: Int) {
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                searchUseCase.execute(query, page)
            }

            when (result) {
                is Try.Success -> processSearchPage(result.value)
                is Try.Failure -> processFailure(result.cause)
            }
        }
    }

    private fun processSearchPage(searchPage: SearchPage) {
        currentPage = searchPage.page
        maxPage = searchPage.total_pages

        if (searchPage.results.isEmpty() && currentPage == FIRST_PAGE) {
            _viewState.value = _viewState.value?.showNoResults(searchQuery)
            return
        }

        val searchResults = searchPage.results
            .filter { searchResult -> searchResult.isMovie() || searchResult.isPerson() }
            .map { searchResult -> searchResult.mapToSearchResultItem() }

        _viewState.value = _viewState.value?.showSearchResult(searchResults)
    }

    private fun processFailure(failure: Try.FailureCause) {
        _viewState.value = when (failure) {
            is Try.FailureCause.NoConnectivity -> _viewState.value?.showNoConnectivityError(
                searchQuery
            ) { performSearch(searchQuery, currentPage) }
            else -> _viewState.value?.showUnknownError(searchQuery) {
                performSearch(searchQuery, currentPage)
            }
        }
    }


    private fun SearchResult.mapToSearchResultItem(): SearchResultItem {
        return SearchResultItem(
            id = id,
            imagePath = extractImagePathFromSearchResult(),
            name = extractTitleFromSearchResult(),
            icon = getIconForSearchResult()
        )
    }

    private fun SearchResult.extractImagePathFromSearchResult() = when (isMovie()) {
        true -> poster_path ?: "Unknown"
        else -> profile_path ?: "Unknown" // case: TV and MOVIE
    }

    private fun SearchResult.extractTitleFromSearchResult() =
        when (isMovie()) {
            true -> title ?: "Unknown"
            else -> name ?: "Unknown" // case: TV and PERSON
        }

    private fun SearchResult.getIconForSearchResult() =
        when (isMovie()) {
            true -> SearchResultTypeIcon.Movie
            else -> SearchResultTypeIcon.Person
        }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
