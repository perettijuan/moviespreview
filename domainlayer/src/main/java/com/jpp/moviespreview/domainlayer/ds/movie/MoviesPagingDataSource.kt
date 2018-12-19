package com.jpp.moviespreview.domainlayer.ds.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePage
import com.jpp.moviespreview.domainlayer.interactor.MoviePageParam
import com.jpp.moviespreview.domainlayer.interactor.MoviePageResult

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
class MoviesPagingDataSource(private val moviePageInteractor: GetMoviePage,
                             private val currentSection: MovieSection) : PageKeyedDataSource<Int, Movie>() {


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
        fetchMoviePage(1) {
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
        fetchMoviePage(params.key) {
            viewStateLiveData.postValue(MoviesDataSourceState.LoadingAfterDone)
            callback.onResult(it, params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        //no-op
    }

    private fun fetchMoviePage(page: Int, callback: (List<Movie>) -> Unit) {
        moviePageInteractor(MoviePageParam(page, currentSection)).let {
            when (it) {
                MoviePageResult.ErrorNoConnectivity -> viewStateLiveData.postValue(MoviesDataSourceState.ErrorNoConnectivity)
                MoviePageResult.ErrorUnknown -> viewStateLiveData.postValue(MoviesDataSourceState.ErrorUnknown)
                is MoviePageResult.Success -> callback.invoke(it.moviePage.movies)
            }
        }
    }
}