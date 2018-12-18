package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.screens.main.movies.paging.MoviesPagingDataSourceFactory
import java.util.concurrent.Executors
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
class MoviesFragmentViewModel @Inject constructor(private val pagingDataSourceFactory: MoviesPagingDataSourceFactory) : ViewModel() {

    private lateinit var viewState: LiveData<MoviesFragmentViewState>

    private lateinit var pagedList: LiveData<PagedList<Movie>>


    fun bindViewState(): LiveData<MoviesFragmentViewState> = viewState

    /**
     * Retrieves a [LiveData] object that is notified when a new [PagedList] is available
     * for rendering.
     *
     * IMPORTANT: this method checks if [pagedList] has been initialized and, if it is, it returns the
     * already initialized object. The use case that this is affecting is rotation: when the
     * device rotates, the Activity gets destroyed, the Fragment gets destroyed but the ViewModel
     * remains the same. When the Fragment is recreated and hook himself to the ViewModel, we want
     * that hooking to the original PagedList and not to a new instance.
     */
    fun getMovieList(movieSection: UiMovieSection, moviePosterSize: Int, movieBackdropSize: Int): LiveData<PagedList<Movie>> {
        if (::pagedList.isInitialized) {
            return pagedList
        }


        pagingDataSourceFactory.config = MoviesPagingDataSourceFactory.MoviesPagingConfig(
                section = movieSection,
                moviePosterSize = moviePosterSize,
                movieBackdropSize = movieBackdropSize
        )

        viewState = Transformations.switchMap(pagingDataSourceFactory.dataSourceLiveData) {
            it.viewStateLiveData
        }

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2) // 2 pre-loads now
                .build()

        pagedList = LivePagedListBuilder(pagingDataSourceFactory, config)
                .setFetchExecutor(Executors.newFixedThreadPool(5))
                .build()

        return pagedList
    }
}