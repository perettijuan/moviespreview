package com.jpp.moviespreview.domainlayer.ds.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.ConfiguredMoviePageParam
import com.jpp.moviespreview.domainlayer.interactor.ConfiguredMoviePageResult
import com.jpp.moviespreview.domainlayer.interactor.GetConfiguredMoviePage
import java.util.concurrent.Executor

/**
 * DataSource (Paging Library) implementation to retrieve the pages of a given [MovieSection].
 * It executes the provided [moviePageInteractor] in order to retrieve the next movie page.
 *
 * In order to model the state of pages retrieval, this class exposes a LiveData element that
 * is notified whenever a new request is initiated and finished (and if an error is detected).
 * Therefore, you can say that this class has two output streams:
 * 1 - One to indicate if it is retrieving information.
 * 2 - One to indicate that a new page is available and needs to be rendered.
 *
 * DESIGN DECISION: This class could be modeled as an interactor that fetches the movie
 * lists from the repository, but I decided to model this DS as a different entity in order to
 * highlight the fact that exists because I'm using the Android Paging Library.
 * Another reason for this decision is that it makes unit testing easier.
 */
class MoviesPagingDataSource(private val moviePage: GetConfiguredMoviePage,
                             private val currentSection: MovieSection,
                             private val backdropSize: Int,
                             private val posterSize: Int,
                             private val retryExecutor: Executor) : PageKeyedDataSource<Int, Movie>() {

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    private val viewStateLiveData by lazy { MutableLiveData<MoviesDataSourceState>() }

    fun getViewState(): LiveData<MoviesDataSourceState> = viewStateLiveData

    /*
     * This method is responsible to load the data initially
     * when app screen is launched for the first time.
     * We are fetching the first page data from the api
     * and passing it via the callback method to the UI.
     */
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        viewStateLiveData.postValue(MoviesDataSourceState.LoadingInitial)
        fetchMoviePage(page = 1, retryFunc = { loadInitial(params, callback) }) {
            viewStateLiveData.postValue(MoviesDataSourceState.LoadingInitialDone)
            callback.onResult(it, null, 2)
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
        viewStateLiveData.postValue(MoviesDataSourceState.LoadingAfter)
        fetchMoviePage(page = params.key, retryFunc = { loadAfter(params, callback) }) {
            viewStateLiveData.postValue(MoviesDataSourceState.LoadingAfterDone)
            callback.onResult(it, params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        //no-op
    }

    /*
     * Ref => https://github.com/googlesamples/android-architecture-components/blob/master/PagingWithNetworkSample/app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inMemory/byPage/PageKeyedSubredditDataSource.kt#L50
     */
    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    private fun fetchMoviePage(page: Int, retryFunc: (() -> Any), callback: (List<Movie>) -> Unit) {
        moviePage(ConfiguredMoviePageParam(page, currentSection, backdropSize, posterSize)).let {
            when (it) {
                ConfiguredMoviePageResult.ErrorNoConnectivity -> {
                    retry = retryFunc
                    viewStateLiveData.postValue(MoviesDataSourceState.ErrorNoConnectivity)
                }
                ConfiguredMoviePageResult.ErrorUnknown -> {
                    retry = retryFunc
                    viewStateLiveData.postValue(MoviesDataSourceState.ErrorUnknown)
                }
                is ConfiguredMoviePageResult.Success -> {
                    retry = null
                    callback.invoke(it.moviePage.movies)
                }
            }
        }
    }
}