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
 * [ViewModel] used to support the search section of the application.
 *
 * This ViewModel controls two different view states:
 *  - The [SearchActivity]' SearchView state.
 *  - The [SearchFragment] content view state.
 *
 * It has the responsibility of handling the two view states because the SearchView that
 * provides searching capabilities is hosted by the [SearchActivity] but the content of the
 * search is actually rendered by [SearchFragment]. This is designed this way because the search
 * module must provide navigation to other sections of the application. In order to achieve that,
 * the application relies in the Navigation Architecture Component that forces to declare a startDestination
 * when instantiated.
 */
class SearchViewModel(
    private val searchUseCase: SearchUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : MPViewModel() {

    private val _searchViewState = MutableLiveData<SearchViewViewState>()
    internal val searchViewState: LiveData<SearchViewViewState> = _searchViewState

    private val _contentViewState = MutableLiveData<SearchContentViewState>()
    internal val contentViewState: LiveData<SearchContentViewState> = _contentViewState

    private var currentPage: Int = 0
    private var maxPage: Int = 0
    private lateinit var searchQuery: String

    /**
     * Called when the view layer is ready to start rendering.
     */
    internal fun onInit() {
        updateCurrentDestination(Destination.MPSearch)

        if (_searchViewState.value == null &&
            _contentViewState.value == null
        ) {
            _searchViewState.value = SearchViewViewState.showCleanState()
            _contentViewState.value = SearchContentViewState.showCleanState()
        }
    }

    /**
     * Called when a search is requested from the view layer.
     */
    internal fun onSearch(query: String) {
        if (::searchQuery.isInitialized && query == searchQuery) {
            return
        }

        searchQuery = query
        _searchViewState.value = SearchViewViewState.showSearching(searchQuery)
        _contentViewState.value = _contentViewState.value?.showSearching()
        performSearch(query, FIRST_PAGE)
    }

    /**
     * Called when a new page in the search is needed.
     */
    internal fun onNextPageRequested() {
        val nextPage = currentPage + 1
        performSearch(searchQuery, nextPage)
    }

    /**
     * Called when the current search is cleared.
     */
    internal fun onClearSearch() {
        searchQuery = ""
        _searchViewState.value = SearchViewViewState.showCleanState()
        _contentViewState.value = SearchContentViewState.showCleanState()
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
            _contentViewState.value = _contentViewState.value?.showNoResults()
            return
        }

        val searchResults = searchPage.results
            .filter { searchResult -> searchResult.isMovie() || searchResult.isPerson() }
            .map { searchResult -> searchResult.mapToSearchResultItem() }

        _contentViewState.value = _contentViewState.value?.showSearchResult(searchResults)
    }

    private fun processFailure(failure: Try.FailureCause) {
        _contentViewState.value = when (failure) {
            is Try.FailureCause.NoConnectivity -> _contentViewState.value?.showNoConnectivityError {
                performSearch(
                    searchQuery,
                    currentPage
                )
            }
            else -> _contentViewState.value?.showUnknownError {
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
