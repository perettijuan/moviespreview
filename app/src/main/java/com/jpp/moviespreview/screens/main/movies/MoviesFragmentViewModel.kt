package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.jpp.mpdomain.repository.OperationState
import com.jpp.mpdomain.repository.movies.MovieListRepository
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
class MoviesFragmentViewModel @Inject constructor(private val movieListRepository: MovieListRepository,
                                                  private val mapper: MovieItemMapper) : ViewModel() {


    private val fetchMovieList = MutableLiveData<MoviesListParam>()
    private val repoResult = map(fetchMovieList) { param ->
        mapper
                .mapMovieSection(param.section)
                .let {
                    movieListRepository.moviePageForSection(it, param.targetBackdropSize, param.targetPosterSize) { domainMovie ->
                        mapper.mapDomainMovie(domainMovie)
                    }
                }

    }

    private val repoState = map(repoResult) { it.operationState }
    val pagedList = switchMap(repoResult) { it.pagedList }

    fun bindViewState(): LiveData<MoviesFragmentViewState> = map(repoState) {
        when (it.value) {
            OperationState.Loading -> MoviesFragmentViewState.Loading
            OperationState.ErrorUnknown -> MoviesFragmentViewState.ErrorUnknown
            OperationState.ErrorNoConnectivity -> MoviesFragmentViewState.ErrorNoConnectivity
            else -> MoviesFragmentViewState.InitialPageLoaded
        }
    }

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
    fun getMovieList(movieSection: UiMovieSection, moviePosterSize: Int, movieBackdropSize: Int) {
//        if (::currentSection.isInitialized
//                && currentSection == movieSection
//                && ::pagedList.isInitialized) {
//            /*
//             * Avoid loading if we're already showing the requested pages.
//             */
//            return pagedList
//        }
//
//        currentSection = movieSection
//
//        val listing = movieListRepository.moviePageOfSection(mapper.mapMovieSection(currentSection),
//                movieBackdropSize,
//                moviePosterSize) { domainMovie -> mapper.mapDomainMovie(domainMovie) }
//
//        pagedList = listing.pagedList
//
//
//        /*
//         * Very cool way to map the ds internal state to a state that the UI understands without coupling the UI to de domain.
//         */
//        repoState = Transformations.map(listing.dataSourceLiveData) {
//            when (it) {
//                MoviesDataSourceState.LoadingInitial -> MoviesFragmentViewState.Loading
//                MoviesDataSourceState.ErrorUnknown -> MoviesFragmentViewState.ErrorUnknown
//                MoviesDataSourceState.ErrorNoConnectivity -> MoviesFragmentViewState.ErrorNoConnectivity
//                else -> MoviesFragmentViewState.InitialPageLoaded
//            }
//        }
//
//        return pagedList
        fetchMovieList.postValue(MoviesListParam(movieSection, movieBackdropSize, moviePosterSize))
    }

    fun retryMoviesListFetch() {
        //pagingDataSourceFactory.retryLastDSCall()
    }


    private data class MoviesListParam(val section: UiMovieSection,
                                       val targetBackdropSize: Int,
                                       val targetPosterSize: Int)
}