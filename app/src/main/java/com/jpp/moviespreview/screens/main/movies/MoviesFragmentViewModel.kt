package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.movies.MoviesRepositoryState
import com.jpp.mpdomain.repository.movies.MovieListRepository

/**
 * [ViewModel] to support the movies list section in the application.
 *
 * This is a very special ViewModel in the application, since it doesn't follows the pattern
 * defined in MPScopedViewModel. This is because this section of the application is using the
 * Paging Library to support unlimited scrolling and the [MovieListRepository] is using its own
 * Executor to defer long term operations to another thread (instead of coroutines provided by
 * the MPScopedViewModel).
 */
abstract class MoviesFragmentViewModel(private val movieListRepository: MovieListRepository) : ViewModel() {


    protected abstract val movieSection: MovieSection
    lateinit var viewState: LiveData<MoviesFragmentViewState>
    lateinit var pagedList: LiveData<PagedList<MovieItem>>
    private lateinit var retryFun: () -> Unit


    fun init(moviePosterSize: Int,
             movieBackdropSize: Int) {

        if (::viewState.isInitialized) {
            return
        }

        movieListRepository.moviePageForSection(movieSection, movieBackdropSize, moviePosterSize) { domainMovie ->
            mapDomainMovie(domainMovie)
        }.let { listing ->
            /*
             * Very cool way to map the ds internal state to a state that the UI understands without coupling the UI to de domain.
             */
            viewState = map(listing.operationState) {
                when (it) {
                    MoviesRepositoryState.Loading -> MoviesFragmentViewState.Loading
                    is MoviesRepositoryState.ErrorUnknown -> {
                        if (it.hasItems) MoviesFragmentViewState.ErrorUnknownWithItems else MoviesFragmentViewState.ErrorUnknown
                    }
                    is MoviesRepositoryState.ErrorNoConnectivity -> {
                        if (it.hasItems) MoviesFragmentViewState.ErrorNoConnectivityWithItems else  MoviesFragmentViewState.ErrorNoConnectivity
                    }
                    MoviesRepositoryState.Loaded -> MoviesFragmentViewState.InitialPageLoaded
                    else -> MoviesFragmentViewState.None
                }
            }

            pagedList = listing.pagedList
            retryFun = listing.retry
        }
    }


    fun retryMoviesListFetch() {
        retryFun.invoke()
    }


    private fun mapDomainMovie(domainMovie: Movie) = with(domainMovie) {
        MovieItem(movieId = id,
                headerImageUrl = backdrop_path ?: "emptyPath",
                title = title,
                contentImageUrl = poster_path ?: "emptyPath",
                popularity = popularity.toString(),
                voteCount = vote_count.toString()
        )
    }
}