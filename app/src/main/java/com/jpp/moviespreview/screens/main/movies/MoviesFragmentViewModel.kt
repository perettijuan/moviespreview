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
 *
 * Check [MoviesPagingDataSource] for a more detailed explanation of the architecture followed
 * in this case.
 */
abstract class MoviesFragmentViewModel(private val movieListRepository: MovieListRepository,
                                       private val mapper: MovieItemMapper,
                                       private val movieSection: MovieSection) : ViewModel() {


    private lateinit var viewState: LiveData<MoviesFragmentViewState>
    private lateinit var pagedList: LiveData<PagedList<MovieItem>>

    /**
     * Retrieves a [LiveData] object that is notified when a new [PagedList] is available
     * for rendering.
     *
     * IMPORTANT: this method checks if [pagedList] has been initialized and, if it is, returns the
     * already initialized object. The use case that this is affecting is rotation: when the
     * device rotates, the Activity gets destroyed, the Fragment gets destroyed but the ViewModel
     * remains the same. When the Fragment is recreated and hook himself to the ViewModel, we want
     * that hooking to the original PagedList and not to a new instance.
     */
    fun getMovieList(moviePosterSize: Int,
                     movieBackdropSize: Int,
                     viewStateBinder: (LiveData<MoviesFragmentViewState>, LiveData<PagedList<MovieItem>>) -> Unit) {


        if (::viewState.isInitialized && ::pagedList.isInitialized) {
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
        viewStateBinder.invoke(viewState, pagedList)
    }

    fun retryMoviesListFetch() {
        //pagingDataSourceFactory.retryLastDSCall()
    }
}