package com.jpp.mp.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.extensions.addAllMapping
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.usecase.ConfigureMovieImagesPathUseCase
import com.jpp.mpdomain.usecase.GetMoviePageUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPViewModel] used to support the movie list section of the application. This ViewModel is shared by
 * the Fragments that show the movies listed in each category that can be displayed. Every time the
 * user selects a section, this VM is refreshed and triggers a new fetching to the underlying layers
 * of the application.
 * Produces different [MovieListViewState] that represents the entire configuration of the screen at any
 * given moment.
 */
class MovieListViewModel @Inject constructor(
        private val getMoviePageUseCase: GetMoviePageUseCase,
        private val configureMovieImagesPathUseCase: ConfigureMovieImagesPathUseCase
) : MPViewModel() {

    private lateinit var movieSection: MovieSection
    private var currentPage: Int = 0
    private var maxPage: Int = 0

    private val _viewState = MutableLiveData<MovieListViewState>()
    val viewState: LiveData<MovieListViewState> get() = _viewState

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(param: MovieListParam) {
        movieSection = param.section
        updateCurrentDestination(Destination.MovieListReached(param.screenTitle))
        _viewState.value = MovieListViewState.showLoading()
        fetchMoviePage(FIRST_PAGE)
    }

    fun onNextMoviePage() {
        val nextPage = currentPage + 1
        fetchMoviePage(nextPage)
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

    private fun fetchMoviePage(page: Int) {
        if (maxPage in 1..page) {
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val firstPageResult = getMoviePageUseCase.execute(page, movieSection)) {
                    is Try.Failure -> processFailure(firstPageResult.cause)
                    is Try.Success -> processMoviePage(firstPageResult.value)
                }
            }
        }
    }

    private fun processFailure(failure: Try.FailureCause) {
        _viewState.value = when (failure) {
            is Try.FailureCause.NoConnectivity -> MovieListViewState.showNoConnectivityError { }
            is Try.FailureCause.Unknown -> MovieListViewState.showUnknownError { }
        }
    }

    private fun processMoviePage(moviePage: MoviePage) {
        val currentMovieListItem = _viewState.value?.contentViewState?.movieList ?: return

        viewModelScope.launch {
            currentPage = moviePage.page
            maxPage = moviePage.total_pages

            val movieList = currentMovieListItem
                    .toMutableList()
                    .addAllMapping {
                        withContext(Dispatchers.IO) {
                            moviePage.results
                                    .map { movie -> movie.configurePath() }
                                    .map { configuredMovie -> configuredMovie.mapToListItem() }
                        }
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
        const val FIRST_PAGE = 1
    }
}
