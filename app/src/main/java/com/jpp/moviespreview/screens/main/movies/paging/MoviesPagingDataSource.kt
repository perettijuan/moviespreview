package com.jpp.moviespreview.screens.main.movies.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.*
import com.jpp.moviespreview.screens.main.movies.MoviesFragmentViewState
import com.jpp.moviespreview.screens.main.movies.UiMovieSection

/**
 * This is the data source that provides to the paging library of items to show in the list.
 *
 * DESIGN DECISION: even though this is a DataSource (from Paging Library), for the current
 * design in MoviesPreview, this class behaves more as a ViewModel in the sense that executes
 * several interactors in order to fetch the movie list from the repository system and adapt
 * the results to the model expected by the UI.
 * Therefore, you should see this class as a UI component and not as a DataSource that should
 * live in the data layer of the application.
 *
 * TODO JPP -> can we use [PageKeyedDataSource#mapByPage] to map the domain results to UI layer results?
 *
 */
class MoviesPagingDataSource(private val moviePageInteractor: GetMoviePageInteractor,
                             private val configureMovieImagesInteractor: ConfigureMovieImagesInteractor,
                             private val currentSection: UiMovieSection,
                             private val moviePosterSize: Int,
                             private val movieBackdropSize: Int) : PageKeyedDataSource<Int, Movie>() {


    val viewStateLiveData by lazy { MutableLiveData<MoviesFragmentViewState>() }
    private val movieSectionMapper: (UiMovieSection) -> MovieSection = {
        when (it) {
            UiMovieSection.Playing -> MovieSection.Playing
            UiMovieSection.Popular -> MovieSection.Popular
            UiMovieSection.TopRated -> MovieSection.TopRated
            UiMovieSection.Upcoming -> MovieSection.Upcoming
        }
    }

    /*
     * This method is responsible to load the data initially
     * when app screen is launched for the first time.
     * We are fetching the first page data from the api
     * and passing it via the callback method to the UI.
     */
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        viewStateLiveData.postValue(MoviesFragmentViewState.Loading)
        fetchMoviePage(1) {
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
        fetchMoviePage(params.key) {
            callback.onResult(it, params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        //no-op
    }

    private fun fetchMoviePage(page: Int, callback: (List<Movie>) -> Unit) {
        moviePageInteractor(MoviePageParam(page, movieSectionMapper.invoke(currentSection))).let {
            when (it) {
                MoviePageResult.ErrorNoConnectivity -> viewStateLiveData.postValue(MoviesFragmentViewState.ErrorNoConnectivity)
                MoviePageResult.ErrorUnknown -> viewStateLiveData.postValue(MoviesFragmentViewState.ErrorNoConnectivity)
                is MoviePageResult.Success -> {
                    it.moviePage.movies
                            .map { movie -> configureMovieImagesInteractor.invoke(MovieImagesParam(movie, movieBackdropSize, moviePosterSize)) }
                            .run {
                                viewStateLiveData.postValue(MoviesFragmentViewState.InitialPageLoaded)
                                callback.invoke(this.map { result -> result.movie })
                            }
                }
            }
        }
    }
}