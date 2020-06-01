package com.jpp.mp.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.usecase.ConfigureMovieImagesPathUseCase
import com.jpp.mpdomain.usecase.GetMoviePageUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [MPViewModel] used to support the movie list section of the application. This ViewModel is shared by
 * the Fragments that show the movies listed in each category that can be displayed. Every time the
 * user selects a section, this VM is refreshed and triggers a new fetching to the underlying layers
 * of the application.
 * Produces different [MovieListViewState] that represents the entire configuration of the screen at any
 * given moment.
 */
class MovieListViewModel(
        private val getMoviePageUseCase: GetMoviePageUseCase,
        private val configureMovieImagesPathUseCase: ConfigureMovieImagesPathUseCase,
        private val ioDispatcher: CoroutineDispatcher,
        private val savedStateHandle: SavedStateHandle
) : MPViewModel() {

    private var movieSection: MovieSection?
        set(value) {
            savedStateHandle.set(MOVIE_SECTION_KEY, value?.name)
        }
        get() {
            return when (val name = (savedStateHandle.get(MOVIE_SECTION_KEY) as String?)) {
                MovieSection.Playing.name -> MovieSection.Playing
                MovieSection.Popular.name -> MovieSection.Popular
                MovieSection.TopRated.name -> MovieSection.TopRated
                MovieSection.Upcoming.name -> MovieSection.Upcoming
                else -> throw IllegalStateException("Can't find section $name")
            }
        }

    private var currentPage: Int
        set(value) {
            savedStateHandle.set(MOVIE_PAGE_KEY, value)
        }
        get() = savedStateHandle.get(MOVIE_PAGE_KEY) ?: 0


    private var maxPage: Int
        set(value) {
            savedStateHandle.set(MAX_MOVIE_PAGE_KEY, value)
        }
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
        updateCurrentDestination(Destination.MovieListReached(screenTitle))
        _viewState.value = MovieListViewState.showLoading()
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
    fun onMovieSelected(movieListItem: MovieListItem) {
        with(movieListItem) {
            navigateTo(Destination.MPMovieDetails(
                    movieId = movieId.toString(),
                    movieImageUrl = contentImageUrl,
                    movieTitle = title)
            )
        }
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
            is Try.FailureCause.NoConnectivity -> MovieListViewState.showNoConnectivityError { onNextMoviePage() }
            is Try.FailureCause.Unknown -> MovieListViewState.showUnknownError { onNextMoviePage() }
        }
    }

    private fun processMoviePage(moviePage: MoviePage) {
        viewModelScope.launch {
            currentPage = moviePage.page
            maxPage = moviePage.total_pages

            val movieList = withContext(ioDispatcher) {
                moviePage.results
                        .map { movie -> movie.configurePath() }
                        .map { configuredMovie -> configuredMovie.mapToListItem() }
            }

            _viewState.value = MovieListViewState.showMovieList(
                    movieList = movieList
            )
        }
    }

    private suspend fun Movie.configurePath(): Movie {
        return configureMovieImagesPathUseCase.execute(this).getOrNull() ?: this
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
        const val MOVIE_PAGE_KEY = "MOVIE_PAGE_KEY"
        const val MAX_MOVIE_PAGE_KEY = "MAX_MOVIE_PAGE_KEY"
        const val FIRST_PAGE = 1
    }
}
