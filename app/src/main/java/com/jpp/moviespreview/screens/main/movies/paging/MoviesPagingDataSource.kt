package com.jpp.moviespreview.screens.main.movies.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePageInteractor
import com.jpp.moviespreview.domainlayer.interactor.MoviePageParam
import com.jpp.moviespreview.domainlayer.interactor.MoviePageResult
import com.jpp.moviespreview.screens.main.movies.MoviesFragmentViewState

/**
 * This is the data source that provides to the paging library of items to show in the list.
 *
 * DESIGN DECISION: even though this is a DataSource for the Paging Library, for the current
 * design in MoviesPreview, this class behaves more as a use case in the sense that executes
 * several interactors in order to fetch the movie list from the repository system and adapt
 * the results to the model expected by the UI.
 * Therefore, you should see this class as a UI component and no as a DataSource that should
 * live in the data layer of the application.
 *
 * TODO JPP -> can we use [PageKeyedDataSource#mapByPage] to map the domain results to UI layer results?
 *
 */
class MoviesPagingDataSource(private val moviePageInteractor: GetMoviePageInteractor,
                             private val currentSection: MovieSection) : PageKeyedDataSource<Int, Movie>() {


    val viewStateLiveData by lazy { MutableLiveData<MoviesFragmentViewState>() }

    /*
     * This method is responsible to load the data initially
     * when app screen is launched for the first time.
     * We are fetching the first page data from the api
     * and passing it via the callback method to the UI.
     */
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {

        viewStateLiveData.postValue(MoviesFragmentViewState.Loading)

        moviePageInteractor(MoviePageParam(1, currentSection)).let {
            when (it) {
                MoviePageResult.ErrorNoConnectivity -> viewStateLiveData.postValue(MoviesFragmentViewState.ErrorNoConnectivity)
                MoviePageResult.ErrorUnknown -> viewStateLiveData.postValue(MoviesFragmentViewState.ErrorNoConnectivity)
                is MoviePageResult.Success -> {
                    viewStateLiveData.postValue(MoviesFragmentViewState.InitialPageLoaded)
                    callback.onResult(it.moviePage.movies, null, 2)
                }
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

        moviePageInteractor(MoviePageParam(params.key, currentSection)).let {
            when (it) {
                MoviePageResult.ErrorNoConnectivity -> viewStateLiveData.postValue(MoviesFragmentViewState.ErrorNoConnectivity)
                MoviePageResult.ErrorUnknown -> viewStateLiveData.postValue(MoviesFragmentViewState.ErrorNoConnectivity)
                is MoviePageResult.Success -> {
                    viewStateLiveData.postValue(MoviesFragmentViewState.InitialPageLoaded)
                    callback.onResult(it.moviePage.movies, params.key + 1)
                }
            }
        }

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        //no-op
    }


}