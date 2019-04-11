package com.jpp.mp.screens.main.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mp.paging.MPPagingDataSourceFactory
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.usecase.account.GetFavoriteMoviesUseCase
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import java.util.concurrent.Executor
import com.jpp.mpdomain.usecase.account.GetFavoriteMoviesUseCase.FavoriteMoviesResult.*

//TODO JPP wire up with FavoriteMoviesFragment
class FavoriteMoviesViewModel(private val favoritesMoviesUseCase: GetFavoriteMoviesUseCase,
                              private val configMovieUseCase: ConfigMovieUseCase,
                              private val networkExecutor: Executor)
    : ViewModel() {


    private val viewState = MediatorLiveData<FavoriteMoviesViewState>()
    private lateinit var retryFunc: (() -> Unit)

    /**
     * Called on initialization of the FavoriteMoviesFragment.
     * Each time this method is called, a new movie list will be fetched from the use case
     * and posted to the viewState, unless a previous list has been fetched.
     */
    fun init(moviePosterSize: Int, movieBackdropSize: Int) {
        if (viewState.value is FavoriteMoviesViewState.Loading
                || viewState.value is FavoriteMoviesViewState.InitialPageLoaded) {
            return
        }


        viewState.value = FavoriteMoviesViewState.Loading
        fetchFreshPage(moviePosterSize, movieBackdropSize)
    }

    /**
     * Starts the process to create the the PagedList that will back the list of movies shown to the
     * user.
     * When the data retrieved from [createPagedList] is obtained, a new state is pushed to viewState().
     */
    private fun fetchFreshPage(moviePosterSize: Int,
                               movieBackdropSize: Int) {
        createPagedList(moviePosterSize, movieBackdropSize).let {
            viewState.addSource(it) { pagedList ->
                if (pagedList.size > 0) {
                    viewState.value = FavoriteMoviesViewState.InitialPageLoaded(pagedList)
                } else {
                    retryFunc = {
                        init(moviePosterSize, movieBackdropSize)
                    }
                }
            }
        }
    }

    /**
     * This is the method that creates the actual [PagedList] that will be used to provide
     * infinite scrolling. It uses a [MPPagingDataSourceFactory] that is mapped to have the
     * desired model ([FavoriteMovieItem]) in order to create the [PagedList].
     *
     * The steps to create the proper instance of [PagedList] are:
     * 1 - Create a [MPPagingDataSourceFactory] instance of type [Movie].
     *      1.1. Create the function that will be used to retry the last movies fetching.
     * 2 - Map the created instance to a second DataSourceFactory of type [Movie], to
     *     execute the configurations of the images path of every result.
     * 3 - Map the instance created in (2) to a new DataSourceFactory that will map the
     *     [Movie] to a [MovieItem].
     */
    private fun createPagedList(moviePosterSize: Int,
                                movieBackdropSize: Int): LiveData<PagedList<FavoriteMovieItem>> {
        return MPPagingDataSourceFactory<Movie> { page, callback -> fetchPage(page, callback) }
                .apply { retryFunc = { networkExecutor.execute { retryLast() } } }
                .map { configMovieUseCase.configure(moviePosterSize, movieBackdropSize, it) }
                .map { mapDomainMovie(it.movie) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(2)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(networkExecutor)
                            .build()
                }
    }

    /**
     * Fetches a favorite movies; page indicated by [page] and invokes the provided [callback] when done.
     * - [page] indicates the current page number to retrieve.
     * - [callback] is a callback executed when the movie fetching us successful. The callback
     *   receives the list of [Movie]s retrieved and the index of the next movies page to fetch.
     * - if an error is detected, then the proper UI update is posted in viewState().
     */
    private fun fetchPage(page: Int, callback: (List<Movie>, Int) -> Unit) {
        favoritesMoviesUseCase
                .getUserFavoriteMovies(page)
                .let { ucResult ->
                    when (ucResult) {
                        is ErrorNoConnectivity -> {
                            viewState.postValue(if (page > 1) FavoriteMoviesViewState.ErrorNoConnectivityWithItems else FavoriteMoviesViewState.ErrorNoConnectivity)
                        }
                        is ErrorUnknown -> {
                            viewState.postValue(if (page > 1) FavoriteMoviesViewState.ErrorUnknownWithItems else FavoriteMoviesViewState.ErrorUnknown)
                        }
                        is UserNotLogged -> viewState.postValue(FavoriteMoviesViewState.UserNotLogged)
                        is NoFavorites -> viewState.postValue(FavoriteMoviesViewState.NoFavorites)
                        is Success -> callback(ucResult.moviesPage.results, page + 1)
                    }
                }
    }

    private fun mapDomainMovie(domainMovie: Movie) = with(domainMovie) {
        FavoriteMovieItem(
                movieId = id,
                headerImageUrl = backdrop_path ?: "emptyPath",
                title = title,
                contentImageUrl = poster_path ?: "emptyPath"
        )
    }

}