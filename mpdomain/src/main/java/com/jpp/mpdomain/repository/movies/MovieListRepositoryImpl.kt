package com.jpp.mpdomain.repository.movies

import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.repository.configuration.ConfigurationApi
import com.jpp.mpdomain.repository.configuration.ConfigurationDb
import com.jpp.mpdomain.paging.MPPagingDataSourceFactory
import java.util.concurrent.Executor

/**
 * [MovieListRepository] implementation.
 * Delegates the paging responsibility to the DataSource created by [MPPagingDataSourceFactory],
 * but holds the responsibility of knowing which set of data should be queried to retrieve
 * the data.
 */
class MovieListRepositoryImpl(private val moviesApi: MoviesApi,
                              private val moviesDb: MoviesDb,
                              private val configurationApi: ConfigurationApi,
                              private val configurationDb: ConfigurationDb,
                              private val connectivityHandler: ConnectivityHandler,
                              private val configurationHandler: ConfigurationHandler,
                              private val networkExecutor: Executor) : MovieListRepository {

    private val operationState by lazy { MutableLiveData<MoviesRepositoryState>() }
    private val postPreFetchOperationState: (page: Int) -> Unit = { page ->
        operationState.postValue(
                when (page == 1) {
                    true -> MoviesRepositoryState.Loading
                    false -> MoviesRepositoryState.None
                }
        )
    }


    override fun <T> moviePageForSection(section: MovieSection, targetBackdropSize: Int, targetPosterSize: Int, mapper: (Movie) -> T): MovieListing<T> {
        val dataSourceFactory = MPPagingDataSourceFactory<Movie> { page, callback ->
            getMoviePageForSection(page, section, callback)
        }

        val retryFunc = { networkExecutor.execute { dataSourceFactory.retryLast() } }

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
                operationState = operationState,
                retry = retryFunc
        )
    }


    private fun getMoviePageForSection(page: Int, section: MovieSection, callback: (List<Movie>, Int) -> Unit) {
        postPreFetchOperationState.invoke(page)
        getMoviePage(page, section)?.let { fetchedMoviePage ->
            operationState.postValue(MoviesRepositoryState.Loaded)
            callback(fetchedMoviePage.results, page + 1)
        } ?: run {
            when (connectivityHandler.isConnectedToNetwork()) {
                true -> operationState.postValue(MoviesRepositoryState.ErrorUnknown(page > 1))
                else -> operationState.postValue(MoviesRepositoryState.ErrorNoConnectivity(page > 1))
            }
        }
    }

    private fun getMoviePage(page: Int, section: MovieSection): MoviePage? {
        return moviesDb.getMoviePageForSection(page, section)?.let {
            it
        } ?: run {
            when (section) {
                MovieSection.Playing -> moviesApi.getNowPlayingMoviePage(page)
                MovieSection.Popular -> moviesApi.getPopularMoviePage(page)
                MovieSection.TopRated -> moviesApi.getTopRatedMoviePage(page)
                MovieSection.Upcoming -> moviesApi.getUpcomingMoviePage(page)
            }?.also {
                moviesDb.saveMoviePageForSection(it, section)
            }
        }
    }


    private fun configureMovieImagesPath(movie: Movie,
                                         targetBackdropSize: Int,
                                         targetPosterSize: Int): Movie {
        return getAppConfiguration()?.let {
            configurationHandler.configureMovieImagesPath(movie, it.images, targetBackdropSize, targetPosterSize)
        } ?: run {
            movie
        }
    }


    private fun getAppConfiguration(): AppConfiguration? {
        return configurationDb.getAppConfiguration()
                ?: run {
                    configurationApi.getAppConfiguration()
                            ?.also { configurationDb.saveAppConfiguration(it) }
                }
    }
}