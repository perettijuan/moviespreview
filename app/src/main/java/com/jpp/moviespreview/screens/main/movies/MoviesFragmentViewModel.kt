package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.RepositoryState
import com.jpp.mpdomain.repository.movies.MovieListRepository

/**
 * [ViewModel] to support the movies list section in the application.
 *
 * This is a very special ViewModel in the application, since it doesn't follows the pattern
 * defined in MPScopedViewModel. This is because this section of the application is using the
 * Paging Library to support unlimited scrolling and that library requires that the DataSource
 * behaves more as a controller in the architecture defined in MoviesPreview.
 */
abstract class MoviesFragmentViewModel(private val movieListRepository: MovieListRepository,
                                       private val mapper: MovieItemMapper,
                                       private val movieSection: MovieSection) : ViewModel() {


    private lateinit var viewState: LiveData<MoviesFragmentViewState>
    private lateinit var pagedList: LiveData<PagedList<MovieItem>>
    private lateinit var retryFun: () -> Unit

    /**
     * Binds two LiveData objects to the provided [viewStateBinder]:
     * - a [MoviesFragmentViewState] LiveData that represents the view state to be rendered.
     * - a [PagedList] of [MovieItem] that will be queried by the Paging Library adapter to fetch
     * new items as the user scrolls.
     */
    fun getMovieListing(moviePosterSize: Int,
                        movieBackdropSize: Int,
                        viewStateBinder: (LiveData<MoviesFragmentViewState>, LiveData<PagedList<MovieItem>>) -> Unit) {


        if (::pagedList.isInitialized && pagedList.value?.isNotEmpty() == true) {
            /*
             * Since the ViewModel survives rotation, we need a way
             * to avoid re-executing the repository on rotation.
             */
            viewStateBinder.invoke(viewState, pagedList)
            return
        }


        val listing = movieListRepository.moviePageForSection(movieSection, movieBackdropSize, moviePosterSize) { domainMovie ->
            mapper.mapDomainMovie(domainMovie)
        }

        /*
         * Very cool way to map the ds internal state to a state that the UI understands without coupling the UI to de domain.
         */
        viewState = map(listing.operationState) {
            when (it) {
                RepositoryState.Loading -> MoviesFragmentViewState.Loading
                RepositoryState.ErrorUnknown -> MoviesFragmentViewState.ErrorUnknown
                RepositoryState.ErrorNoConnectivity -> MoviesFragmentViewState.ErrorNoConnectivity
                RepositoryState.Loaded -> MoviesFragmentViewState.InitialPageLoaded
                else -> MoviesFragmentViewState.None
            }
        }

        pagedList = listing.pagedList
        retryFun = listing.retry
        viewStateBinder.invoke(viewState, pagedList)
    }

    fun retryMoviesListFetch() {
        retryFun.invoke()
    }
}