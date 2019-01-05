package com.jpp.mpdomain.repository.movies

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.jpp.moviespreview.common.extensions.and
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.repository.*
import java.util.concurrent.Executor


class MovieListRepositoryImpl(private val moviesApi: MoviesApi,
                              private val moviesDb: MoviesDb,
                              private val configurationApi: ConfigurationApi,
                              private val configurationDb: ConfigurationDb,
                              private val connectivityHandler: ConnectivityHandler,
                              private val configurationHandler: ConfigurationHandler,
                              private val networkExecutor: Executor) : MovieListRepository {

    private val operationState by lazy { MutableLiveData<OperationState>() }


    override fun <T> moviePageForSection(section: MovieSection, targetBackdropSize: Int, targetPosterSize: Int, mapper: (Movie) -> T): MovieListing<T> {
        //TODO JPP -> you have to map retryAllFailed

        val dataSourceFactory = GetMoviesDataSourceFactory { page, callback ->
            fetchMoviePage(page, section, callback)
        }

        val pagedList = dataSourceFactory
                .map { configureMovieImagesPath(it, targetBackdropSize, targetPosterSize) }
                .map { mapper(it) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setEnablePlaceholders(true) //This does actually nothing (and it sucks). -> placeholders can only be enabled if your DataSource provides total items count.
                            .setPrefetchDistance(1)
                            .build()

                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(networkExecutor)
                            .build()
                }


        return MovieListing(
                pagedList = pagedList,
                operationState = operationState
        )
    }


    private fun fetchMoviePage(page: Int, section: MovieSection, callback: (List<Movie>, Int) -> Unit) {
        //TODO JPP -> improve this code
        when (connectivityHandler.isConnectedToNetwork()) {
            true -> {
                if (page == 1) {
                    operationState.postValue(OperationState.Loading)
                }

                moviesDb.getMoviePageForSection(page, section)
                        ?.let {
                            operationState.postValue(OperationState.Loaded)
                            callback(it.results, page + 1)
                        }
                        ?: run {
                            fetchAndStoreMoviePage(page, section)
                        }?.let {
                            operationState.postValue(OperationState.Loaded)
                            callback(it, page + 1)
                        }
                        ?: run {
                            operationState.postValue(OperationState.ErrorUnknown)
                        }
            }
            else -> operationState.postValue(OperationState.ErrorNoConnectivity)
        }
    }


    private fun fetchAndStoreMoviePage(page: Int, section: MovieSection): List<Movie>? {
        return when (section) {
            MovieSection.Playing -> moviesApi.getNowPlayingMoviePage(page)
            MovieSection.Popular -> moviesApi.getPopularMoviePage(page)
            MovieSection.TopRated -> moviesApi.getTopRatedMoviePage(page)
            MovieSection.Upcoming -> moviesApi.getUpcomingMoviePage(page)
        }?.let {
            moviesDb.saveMoviePageForSection(it, section)
            it.results
        }
    }


    private fun configureMovieImagesPath(movie: Movie,
                                         targetBackdropSize: Int,
                                         targetPosterSize: Int): Movie {

        return getAppConfiguration()
                ?.let {
                    configurationHandler.configureMovie(movie, it.images, targetBackdropSize, targetPosterSize)
                }
                ?: run { movie }
    }


    private fun getAppConfiguration(): AppConfiguration? {
        return configurationDb.getAppConfiguration()
                ?: run {
                    configurationApi.getAppConfiguration()?.also { configurationDb.saveAppConfiguration(it) }
                }
    }


    private inner class GetMoviesDataSourceFactory(fetchItems: (Int, (List<Movie>, Int) -> Unit) -> Unit)
        : DataSource.Factory<Int, Movie>() {

        private val dataSource: GetMoviesDataSource = GetMoviesDataSource(fetchItems)

        /**
         * From [DataSource.Factory#create()]
         */
        override fun create(): DataSource<Int, Movie> {
            return dataSource
        }
    }


    private inner class GetMoviesDataSource(private val fetchItems: (Int, (List<Movie>, Int) -> Unit) -> Unit) : PageKeyedDataSource<Int, Movie>() {

        /*
         * This method is responsible to load the data initially
         * when app screen is launched for the first time.
         * We are fetching the first page data from the api
         * and passing it via the callback method to the UI.
         */
        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
            fetchItems(1) { movieList, nextPage ->
                callback.onResult(movieList, null, nextPage)
            }
        }

        /*
         * This method it is responsible for the subsequent call to load the data page wise.
         * This method is executed in the background thread
         * We are fetching the next page data from the api
         * and passing it via the callback method to the UI.
         * The "params.key" variable will have the updated value.
         */
        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
            fetchItems(params.key) { movieList, nextPage ->
                callback.onResult(movieList, nextPage)
            }
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
            //no-op
        }
    }
}