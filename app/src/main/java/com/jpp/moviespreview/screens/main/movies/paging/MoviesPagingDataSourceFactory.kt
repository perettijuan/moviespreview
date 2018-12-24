package com.jpp.moviespreview.screens.main.movies.paging

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.ds.movie.MoviesDataSourceState
import com.jpp.moviespreview.domainlayer.ds.movie.MoviesPagingDataSource
import com.jpp.moviespreview.domainlayer.interactor.GetConfiguredMoviePage
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePage
import java.util.concurrent.Executors
import javax.inject.Inject


/**
 * Factory class to create the DataSource that will provide the pages to show in the movies section
 * of the application. An instance of this class is injected into the ViewModels to hook the
 * callbacks to the UI.
 *
 * This class represents the middle ground (can be seen as a mediator) between the UI layer and the
 * Domain layer when it comes to movie lists:
 * It receives commands from the ViewModels and creates new dataSource instances as needed.
 */
class MoviesPagingDataSourceFactory @Inject constructor(private val moviePage: GetConfiguredMoviePage) : DataSource.Factory<Int, Movie>() {

    // the current MoviesPagingDataSource being used.
    private lateinit var dataSource: MoviesPagingDataSource
    // used to map the state of MoviesPagingDataSource to UI state.
    lateinit var dataSourceLiveData: LiveData<MoviesDataSourceState>

    /**
     * Called to retrieve the [PagedList] that will hold the list of movies retrieved from the server.
     * [movieSection] indicates the section needed.
     * [mapper] a mapping function to transform domain objects into another layer objects.
     */
    fun <T> getMovieList(movieSection: MovieSection,
                         backdropSize: Int,
                         posterSize: Int,
                         mapper: (Movie) -> T): LiveData<PagedList<T>> {
        if (::dataSource.isInitialized) {
            /*
             * This method enforces a new call to create() in order to hook up the newly created
             * ds. Whenever a ds gets invalidated, the Paging Library marks it as invalid to retrieve
             * data and aks the factory to provide a new instance.
             */
            dataSource.invalidate()
        }

        dataSource = MoviesPagingDataSource(moviePage, movieSection, backdropSize, posterSize, Executors.newFixedThreadPool(5))
        dataSourceLiveData = dataSource.getViewState()

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true) //This does actually nothing (and it sucks). -> placeholders can only be enabled if your DataSource provides total items count.
                .setPrefetchDistance(1)
                .build()

        return LivePagedListBuilder(map { mapper.invoke(it) }, config)
                .setFetchExecutor(Executors.newFixedThreadPool(5))
                .build()
    }

    fun retryLastDSCall() {
        if (::dataSource.isInitialized) {
            dataSource.retryAllFailed()
        }
    }

    /**
     * From [DataSource.Factory#create()]
     */
    override fun create(): DataSource<Int, Movie> {
        if (::dataSource.isInitialized) {
            return dataSource
        }
        throw IllegalStateException("You need to provide a section to initialize the data source")
    }

}