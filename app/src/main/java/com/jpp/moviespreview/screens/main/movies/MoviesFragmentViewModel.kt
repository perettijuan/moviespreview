package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import com.jpp.moviespreview.paging.MPPagingDataSourceFactory
import com.jpp.moviespreview.screens.SingleLiveEvent
import com.jpp.moviespreview.screens.main.search.SearchViewNavigationEvent
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCaseResult
import java.util.concurrent.Executor

/**
 * [ViewModel] to support the movies list section in the application.
 *
 * It exposes a single output in a LiveData object that receives [MoviesViewState] updates as soon
 * as any new state is identified by the ViewModel.
 */
abstract class MoviesFragmentViewModel(private val getMoviesUseCase: GetMoviesUseCase,
                                       private val configMovieUseCase: ConfigMovieUseCase,
                                       private val networkExecutor: Executor) : ViewModel() {

    protected abstract val movieSection: MovieSection

    private val viewState = MediatorLiveData<MoviesViewState>()
    private val navigationEvents  by lazy { SingleLiveEvent<MoviesViewNavigationEvent>() }
    private var isInitialized = false
    private lateinit var retryFunc: (() -> Unit)

    /**
     * Called on initialization of the movies fragment.
     * Each time this method is called, a new movie list will be fetched from the use case
     * and posted to the viewState, unless a previous list has been fetched.
     */
    fun init(moviePosterSize: Int, movieBackdropSize: Int) {
        if (isInitialized) {
            return
        }

        isInitialized = true
        viewState.value = MoviesViewState.Loading
        createMoviesPagedList(moviePosterSize, movieBackdropSize).let {
            viewState.addSource(it) { pagedList ->
                if (pagedList.size > 0) {
                    viewState.value = MoviesViewState.InitialPageLoaded(pagedList)
                } else {
                    retryFunc = {
                        isInitialized = false
                        init(moviePosterSize, movieBackdropSize)
                    }
                }
            }
        }
    }

    /**
     * Exposes a stream that is updated with a new [MoviesViewState]
     * each time that a new state is identified.
     */
    fun viewState(): LiveData<MoviesViewState> = viewState

    /**
     * Exposes the events that are triggered when a navigation event is detected.
     * We need a different LiveData here in order to avoid the problem of back navigation:
     * - The default LiveData object posts the last value every time a new observer starts observing.
     */
    fun navEvents(): LiveData<MoviesViewNavigationEvent> = navigationEvents

    /**
     * Called when an item is selected in the list of movies.
     * A new state is posted in navEvents() in order to handle the event.
     */
    fun onMovieSelected(movieItem: MovieItem) {
        with(movieItem) {
            navigationEvents.value = MoviesViewNavigationEvent.ToMovieDetails(
                    movieId = movieId.toString(),
                    movieImageUrl = contentImageUrl,
                    movieTitle = title)
        }
    }

    /**
     * Attempts to execute the last movie fetching step that was executed. Typically called after an error
     * is detected.
     */
    fun retryMoviesFetch() {
        retryFunc.invoke()
    }

    /**
     * This is the method that creates the actual [PagedList] that will be used to provide
     * infinite scrolling. It uses a [MPPagingDataSourceFactory] that is mapped to have the
     * desired model ([MovieItem]) in order to create the [PagedList].
     *
     * The steps to create the proper instance of [PagedList] are:
     * 1 - Create a [MPPagingDataSourceFactory] instance of type [Movie].
     *      1.1. Create the function that will be used to retry the last movies fetching.
     * 2 - Map the created instance to a second DataSourceFactory of type [Movie], to
     *     execute the configurations of the images path of every result.
     * 3 - Map the instance created in (2) to a new DataSourceFactory that will map the
     *     [Movie] to a [MovieItem].
     */
    private fun createMoviesPagedList(moviePosterSize: Int,
                                      movieBackdropSize: Int): LiveData<PagedList<MovieItem>> {
        return MPPagingDataSourceFactory<Movie> { page, callback -> fetchMoviePage(page, callback) } // (1)
                .apply { retryFunc = { networkExecutor.execute { retryLast() } } } // (1.1)
                .map { configMovieUseCase.configure(moviePosterSize, movieBackdropSize, it) }// (2)
                .map { mapDomainMovie(it) } // (3)
                .let {
                    // build the PagedList
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(3)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(networkExecutor)
                            .build()
                }
    }

    /**
     * Fetches a movies page indicated by [page] and invokes the provided [callback] when done.
     * - [page] indicates the current page number to retrieve.
     * - [callback] is a callback executed when the movie fetching us successful. The callback
     *   receives the list of [Movie]s retrieved and the index of the next movies page to fetch.
     */
    private fun fetchMoviePage(page: Int, callback: (List<Movie>, Int) -> Unit) {
        getMoviesUseCase
                .getMoviePageForSection(page, movieSection)
                .let { ucResult ->
                    when (ucResult) {
                        is GetMoviesUseCaseResult.ErrorNoConnectivity -> {
                            viewState.value = if (page > 1) MoviesViewState.ErrorNoConnectivityWithItems else MoviesViewState.ErrorNoConnectivity
                        }
                        is GetMoviesUseCaseResult.ErrorUnknown -> {
                            viewState.value = if (page > 1) MoviesViewState.ErrorUnknownWithItems else MoviesViewState.ErrorUnknown
                        }
                        is GetMoviesUseCaseResult.Success -> {
                            callback(ucResult.moviesPage.results, page + 1)
                        }
                    }
                }
    }

    private fun mapDomainMovie(domainMovie: Movie) = with(domainMovie) {
        MovieItem(movieId = id,
                headerImageUrl = backdrop_path ?: "emptyPath",
                title = title,
                contentImageUrl = poster_path ?: "emptyPath",
                popularity = popularity.toString(),
                voteCount = vote_count.toString()
        )
    }
}