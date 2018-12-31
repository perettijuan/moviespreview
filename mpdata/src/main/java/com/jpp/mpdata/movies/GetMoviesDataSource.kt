package com.jpp.mpdata.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.jpp.mpdata.ConnectivityHandler
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.OperationState

/**
 * [PageKeyedDataSource] implementation to retrieve the pages of a given [MovieSection].
 * Provides a catching mechanism:
 *          - Verifies if the page to retrieve is stored in the database.
 *          - If it is stored, it executes the callback with the data in the DB.
 *          - If not stored, retrieves the data from the api and updates the local storage before executing the callback.
 * It provides a [LiveData] object that maps the state of the operation being executed by the datasource.
 */
class GetMoviesDataSource(private val currentSection: MovieSection,
                          private val moviesApi: MoviesApi,
                          private val moviesDb: MoviesDb,
                          private val connectivityHandler: ConnectivityHandler,
                          private val movieImagesConfigurator: ((Movie) -> Movie)) : PageKeyedDataSource<Int, Movie>() {

    private val operationState by lazy { MutableLiveData<OperationState>() }

    /*
     * This method is responsible to load the data initially
     * when app screen is launched for the first time.
     * We are fetching the first page data from the api
     * and passing it via the callback method to the UI.
     */
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        operationState.postValue(OperationState.Loading)
        fetchMoviePage(page = 1, section = currentSection) { movieList ->
            movieList
                    .map { movie -> movieImagesConfigurator(movie) }
                    .let { configuredMovieList ->
                        operationState.postValue(OperationState.Loaded)
                        callback.onResult(configuredMovieList, null, 2)
                    }
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
        fetchMoviePage(page = params.key, section = currentSection) { movieList ->
            movieList
                    .map { movie -> movieImagesConfigurator(movie) }
                    .let { configuredMovieList ->
                        operationState.postValue(OperationState.Loaded)
                        callback.onResult(configuredMovieList, params.key + 1)
                    }
        }
    }


    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        //no-op
    }

    fun getOperationState(): LiveData<OperationState> = operationState


    private fun fetchMoviePage(page: Int, section: MovieSection, callback: (List<Movie>) -> Unit) {
        when (connectivityHandler.isConnectedToNetwork()) {
            true -> moviesDb.getMoviePageForSection(page, section)
                    ?.let { callback(it.results) }
                    ?: run { fetchAndStoreMoviePage(page, section, callback) }
            else -> operationState.postValue(OperationState.ErrorNoConnectivity)
        }
    }

    private fun fetchAndStoreMoviePage(page: Int, section: MovieSection, callback: (List<Movie>) -> Unit) {
        when (section) {
            MovieSection.Playing -> moviesApi.getNowPlayingMoviePage(page)
            MovieSection.Popular -> moviesApi.getPopularMoviePage(page)
            MovieSection.TopRated -> moviesApi.getTopRatedMoviePage(page)
            MovieSection.Upcoming -> moviesApi.getUpcomingMoviePage(page)
        }?.let {
            moviesDb.saveMoviePageForSection(it, section)
            callback(it.results)
        } ?: run {
            operationState.postValue(OperationState.ErrorUnknown)
        }
    }
}