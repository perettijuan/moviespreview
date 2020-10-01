package com.jpp.mp.main.discover

import androidx.lifecycle.*
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.usecase.GetAllMovieGenresUseCase
import com.jpp.mpdomain.usecase.GetDiscoveredMoviePageUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * TODO JPP add javadoc
 */
class DiscoverMoviesViewModel(
    private val getDiscoveredMoviePageUseCase: GetDiscoveredMoviePageUseCase,
    private val gentAllMovieGenresUseCase: GetAllMovieGenresUseCase,
    private val navigator: DiscoverMoviesNavigator,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /*
     * Not stored in SavedStateHandle b/c we want to recreate
     * the whole paging when app killed.
     */
    private var currentPage: Int = 0

    private var maxPage: Int
        set(value) = savedStateHandle.set(MAX_MOVIE_PAGE_KEY, value)
        get() = savedStateHandle.get(MAX_MOVIE_PAGE_KEY) ?: 0

    private val _viewState = MutableLiveData<DiscoverMoviesViewState>()
    val viewState: LiveData<DiscoverMoviesViewState> = _viewState

    private val _filtersViewState = MutableLiveData<DiscoverMoviesFiltersViewState>()
    val filterViewState: LiveData<DiscoverMoviesFiltersViewState> = _filtersViewState

    /**
     * Called on VM initialization. The View (Fragment) calls this method to
     * indicate that the view is ready to render any view states. When the method
     * is called, the VM verifies the application's internal state and updates
     * the view state based on that state.
     */
    fun onInit() {
        _viewState.value = DiscoverMoviesViewState.showLoading()
        _filtersViewState.value = DiscoverMoviesFiltersViewState.showLoading()
        fetchMoviePage(FIRST_PAGE)
    }

    /**
     * Called when the next movie page should be retrieved.
     */
    fun onNextMoviePage() {
        val nextPage = currentPage + 1
        fetchMoviePage(nextPage)
    }

    /**
     * Called when an item is selected in the list of movies.
     */
    fun onMovieSelected(item: DiscoveredMovieListItem) {
        navigator.navigateToMovieDetails(
            item.movieId.toString(),
            item.contentImageUrl,
            item.title
        )
    }

    /**
     * Called when the expand/collapse section is clicked in the filters
     * section.
     */
    fun onFilterExpandClicked(isExpanded: Boolean) {
        _filtersViewState.value = _filtersViewState.value?.copy(isExpanded = !isExpanded)
    }

    private fun fetchMoviePage(page: Int) {
        // page is higher (or lower) than maxPage
        if (maxPage in 1..page) return

        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getDiscoveredMoviePageUseCase.execute(page)
            }
            when (result) {
                is Try.Success -> {
                    fetchFilters() // fetch filters only if the discover has been successful
                    processMoviePage(result.value)
                }
                is Try.Failure -> processFailure(result.cause)
            }
        }
    }

    private fun fetchFilters() {
        viewModelScope.launch {
            val genresResult = withContext(ioDispatcher) {
                gentAllMovieGenresUseCase.execute()
            }

            if (genresResult is Try.Success) {
                _filtersViewState.value = _filtersViewState.value?.showVisible()
            }
        }
    }

    private fun processMoviePage(moviePage: MoviePage) {
        currentPage = moviePage.page
        maxPage = moviePage.total_pages
        viewModelScope.launch {
            val movieList = withContext(ioDispatcher) {
                moviePage.results
                    .map { movie -> movie.toDiscoveredItem() }
            }
            _viewState.value = _viewState.value?.showMovieList(movieList)
        }
    }

    private fun processFailure(failure: Try.FailureCause) {
        _viewState.value = when (failure) {
            is Try.FailureCause.NoConnectivity -> _viewState.value?.showNoConnectivityError { onNextMoviePage() }
            else -> _viewState.value?.showUnknownError { onNextMoviePage() }
        }
    }

    private fun Movie.toDiscoveredItem() =
        DiscoveredMovieListItem(
            movieId = id,
            headerImageUrl = backdrop_path ?: "emptyPath",
            title = title,
            contentImageUrl = poster_path ?: "emptyPath",
            popularity = popularity.toString(),
            voteCount = vote_count.toString()
        )

    private companion object {
        const val MAX_MOVIE_PAGE_KEY = "MAX_MOVIE_PAGE_KEY"
        const val FIRST_PAGE = 1
    }
}