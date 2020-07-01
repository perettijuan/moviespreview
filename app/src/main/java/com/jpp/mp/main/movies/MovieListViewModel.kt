package com.jpp.mp.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.usecase.GetMoviePageUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] used to support the movie list section of the application. This ViewModel is shared by
 * the Fragments that show the movies listed in each category that can be displayed. Every time the
 * user selects a section, this VM is refreshed and triggers a new fetching to the underlying layers
 * of the application.
 * Produces different [MovieListViewState] that represents the entire configuration of the screen at any
 * given moment.
 */
class MovieListViewModel(
    private val getMoviePageUseCase: GetMoviePageUseCase,
    private val navigator: MovieListNavigator,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var movieSection: MovieSection?
        set(value) = savedStateHandle.set(MOVIE_SECTION_KEY, value?.name)
        get() {
            return when (val name = (savedStateHandle.get(MOVIE_SECTION_KEY) as String?)) {
                MovieSection.Playing.name -> MovieSection.Playing
                MovieSection.Popular.name -> MovieSection.Popular
                MovieSection.TopRated.name -> MovieSection.TopRated
                MovieSection.Upcoming.name -> MovieSection.Upcoming
                else -> throw IllegalStateException("Can't find section $name")
            }
        }

    /*
     * Not stored in SavedStateHandle b/c we want to recreate
     * the whole paging when app killed.
     */
    private var currentPage: Int = 0

    private var maxPage: Int
        set(value) = savedStateHandle.set(MAX_MOVIE_PAGE_KEY, value)
        get() = savedStateHandle.get(MAX_MOVIE_PAGE_KEY) ?: 0

    private val _viewState = MutableLiveData<MovieListViewState>()
    val viewState: LiveData<MovieListViewState> = _viewState

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(section: MovieSection, screenTitle: String) {
        movieSection = section
        _viewState.value = MovieListViewState.showLoading(screenTitle)
        fetchMoviePage(FIRST_PAGE, movieSection)
    }

    /**
     * Called when the next movie page should be retrieved.
     */
    fun onNextMoviePage() {
        val nextPage = currentPage + 1
        fetchMoviePage(nextPage, movieSection)
    }

    /**
     * Called when an item is selected in the list of movies.
     */
    fun onMovieSelected(item: MovieListItem) {
        navigator.navigateToMovieDetails(
            item.movieId.toString(),
            item.contentImageUrl,
            item.title
        )
    }

    /**
     * Called when the search option is selected.
     */
    fun onSearchOptionSelected() {
        navigator.navigateToSearch()
    }

    /**
     * Called when the about option is selected.
     */
    fun onAboutOptionSelected() {
        navigator.navigateToAboutSection()
    }

    private fun fetchMoviePage(page: Int, movieSection: MovieSection?) {
        if (movieSection == null) {
            throw NullPointerException("movieSection is NULL for some reason")
        }

        if (maxPage in 1..page) return
        viewModelScope.launch {
            val pageResult = withContext(ioDispatcher) {
                getMoviePageUseCase.execute(page, movieSection)
            }
            when (pageResult) {
                is Try.Failure -> processFailure(pageResult.cause)
                is Try.Success -> processMoviePage(pageResult.value)
            }
        }
    }

    private fun processFailure(failure: Try.FailureCause) {
        _viewState.value = when (failure) {
            is Try.FailureCause.NoConnectivity -> _viewState.value?.showNoConnectivityError { onNextMoviePage() }
            else -> _viewState.value?.showUnknownError { onNextMoviePage() }
        }
    }

    private fun processMoviePage(moviePage: MoviePage) {
        viewModelScope.launch {
            currentPage = moviePage.page
            maxPage = moviePage.total_pages

            val movieList = withContext(ioDispatcher) {
                moviePage.results
                    .map { configuredMovie -> configuredMovie.mapToListItem() }
            }

            _viewState.value = viewState.value?.showMovieList(
                movieList = movieList
            )
        }
    }

    private fun Movie.mapToListItem() =
        MovieListItem(
            movieId = id,
            headerImageUrl = backdrop_path ?: "emptyPath",
            title = title,
            contentImageUrl = poster_path ?: "emptyPath",
            popularity = popularity.toString(),
            voteCount = vote_count.toString()
        )

    private companion object {
        const val MOVIE_SECTION_KEY = "MOVIE_SECTION_KEY"
        const val MAX_MOVIE_PAGE_KEY = "MAX_MOVIE_PAGE_KEY"
        const val FIRST_PAGE = 1
    }
}
