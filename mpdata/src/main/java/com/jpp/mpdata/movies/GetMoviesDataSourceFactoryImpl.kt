package com.jpp.mpdata.movies

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.repository.movies.MoviesApi
import com.jpp.mpdomain.repository.movies.MoviesDb
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.OperationState
import com.jpp.mpdomain.repository.movies.GetMoviesDataSourceFactory
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TODO JPP -> can we make this logic live in GetMovieListRepository?
 * I think so because this guy should live in the domain layer and BE the repository.
 *
 * [GetMoviesDataSourceFactory] implementation to provide a [PagedList] of the movies to show.
 * It implements [DataSource.Factory] in order to be used with the paging library.
 */
class GetMoviesDataSourceFactoryImpl(private val moviesApi: MoviesApi,
                                     private val moviesDb: MoviesDb,
                                     private val connectivityHandler: ConnectivityHandler,
                                     private val networkExecutor: Executor)
    : GetMoviesDataSourceFactory,
        DataSource.Factory<Int, Movie>() {

    // the current MoviesPagingDataSource being used.
    private lateinit var dataSource: GetMoviesDataSource
    // used to map the state of MoviesPagingDataSource to UI state.
    private lateinit var dataSourceOperationState: LiveData<OperationState>

    /**
     * From [DataSource.Factory#create()]
     */
    override fun create(): DataSource<Int, Movie> {
        if (::dataSource.isInitialized) {
            return dataSource
        }
        throw IllegalStateException("You need to provide a section to initialize the data source")
    }

    override fun <T> getMoviesForSection(movieSection: MovieSection,
                                         movieImagesConfigurator: ((Movie) -> Movie),
                                         movieMapper: (Movie) -> T): LiveData<PagedList<T>> {
        if (::dataSource.isInitialized) {
            //TODO JPP -> Can I remove this? Otherwise, is it possible to update the comment to make it more explicit?
            /*
             * This method enforces a new call to create() in order to hook up the newly created
             * ds. Whenever a ds gets invalidated, the Paging Library marks it as invalid to retrieve
             * data and aks the factory to provide a new instance.
             */
            dataSource.invalidate()
        }

        dataSource = GetMoviesDataSource(currentSection = movieSection,
                moviesApi = moviesApi,
                moviesDb = moviesDb,
                connectivityHandler = connectivityHandler,
                movieImagesConfigurator = movieImagesConfigurator)

        dataSourceOperationState = dataSource.getOperationState()

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true) //This does actually nothing (and it sucks). -> placeholders can only be enabled if your DataSource provides total items count.
                .setPrefetchDistance(1)
                .build()

        return LivePagedListBuilder(map { movieMapper.invoke(it) }, config)
                .setFetchExecutor(networkExecutor)
                .build()
    }

    override fun getOperationStateLiveData(): LiveData<OperationState> = dataSourceOperationState
}