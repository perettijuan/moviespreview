package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.jpp.moviespreview.domainlayer.ds.movie.MoviesDataSourceState
import com.jpp.moviespreview.screens.main.movies.paging.MoviesPagingDataSourceFactory
import javax.inject.Inject

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
class MoviesFragmentViewModel @Inject constructor(private val pagingDataSourceFactory: MoviesPagingDataSourceFactory,
                                                  private val mapper: MovieItemMapper) : ViewModel() {

    private lateinit var viewState: LiveData<MoviesFragmentViewState>

    private lateinit var pagedList: LiveData<PagedList<MovieItem>>

    private lateinit var currentSection: UiMovieSection


    fun bindViewState(): LiveData<MoviesFragmentViewState> = viewState

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
    fun getMovieList(movieSection: UiMovieSection, moviePosterSize: Int, movieBackdropSize: Int): LiveData<PagedList<MovieItem>> {
        if (::currentSection.isInitialized
                && currentSection == movieSection
                && ::pagedList.isInitialized) {
            /*
             * Avoid loading if we're already showing the requested pages.
             */
            return pagedList
        }

        currentSection = movieSection

        pagedList = pagingDataSourceFactory.getMovieList(mapper.mapMovieSection(currentSection),
                movieBackdropSize,
                moviePosterSize) { domainMovie ->
            mapper.mapDomainMovie(domainMovie)
        }


        /*
         * Very cool way to map the ds internal state to a state that the UI understands without coupling the UI to de domain.
         */
        viewState = Transformations.map(pagingDataSourceFactory.dataSourceLiveData) {
            when (it) {
                MoviesDataSourceState.LoadingInitial -> MoviesFragmentViewState.Loading
                MoviesDataSourceState.ErrorUnknown -> MoviesFragmentViewState.ErrorUnknown
                MoviesDataSourceState.ErrorNoConnectivity -> MoviesFragmentViewState.ErrorNoConnectivity
                else -> MoviesFragmentViewState.InitialPageLoaded
            }
        }

        return pagedList
    }

    fun retryMoviesListFetch() {
        pagingDataSourceFactory.retryLastDSCall()
    }
}