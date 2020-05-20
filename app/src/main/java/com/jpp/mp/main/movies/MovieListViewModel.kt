package com.jpp.mp.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mp.common.coroutines.CoroutineExecutor
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mp.common.paging.MPPagingDataSourceFactory
import com.jpp.mp.main.movies.MovieListInteractor.MovieListEvent.NotConnectedToNetwork
import com.jpp.mp.main.movies.MovieListInteractor.MovieListEvent.UnknownError
import com.jpp.mp.main.movies.MovieListInteractor.MovieListEvent.UserChangedLanguage
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPViewModel] used to support the movie list section of the application. This ViewModel is shared by
 * the Fragments that show the movies listed in each category that can be displayed. Every time the
 * user selects a section, this VM is refreshed and triggers a new fetching to the underlying layers
 * of the application.
 * Produces different [MovieListViewState] that represents the entire configuration of the screen at any
 * given moment.
 *
 * Since the UI is using the Android Paging Library, the VM needs a way to map the data retrieved from
 * the [MovieListInteractor] to a [PagedList] that can be used by the library. That process is done
 * using the [MPPagingDataSourceFactory] that creates the DataSource and produces a [LiveData] object
 * that is combined with the [viewState] in order to properly map the data into a [MovieListViewState].
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class MovieListViewModel @Inject constructor(
        private val movieListInteractor: MovieListInteractor,
        private val imagesPathInteractor: ImagesPathInteractor
) : MPViewModel() {

    private val _viewState = MediatorLiveData<MovieListViewState>()
    val viewState: LiveData<MovieListViewState> get() = _viewState

    private lateinit var currentParam: MovieListParam

    private val retry: () -> Unit = {
        postLoadingAndInitializePagedList(
                currentParam.posterSize,
                currentParam.backdropSize,
                currentParam.section
        )
    }

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewState.addSource(movieListInteractor.events) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewState.value = MovieListViewState.showNoConnectivityError(retry)
                is UnknownError -> _viewState.value = MovieListViewState.showUnknownError(retry)
                is UserChangedLanguage -> refreshData()
            }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(param: MovieListParam) {
        currentParam = param
        updateCurrentDestination(Destination.MovieListReached(param.screenTitle))

        postLoadingAndInitializePagedList(
                currentParam.posterSize,
                currentParam.backdropSize,
                currentParam.section
        )
    }

    /**
     * Called when an item is selected in the list of movies.
     */
    fun onMovieSelected(movieListItem: MovieListItem) {
        with(movieListItem) {
            navigateTo(Destination.MPMovieDetails(
                    movieId = movieId.toString(),
                    movieImageUrl = contentImageUrl,
                    movieTitle = title)
            )
        }
    }

    /**
     * Pushes the Loading view state into the view layer and creates the [PagedList]
     * of [MovieListItem] that will be rendered by the view layer.
     */
    private fun postLoadingAndInitializePagedList(posterSize: Int, backdropSize: Int, section: MovieSection) {
        _viewState.value = MovieListViewState.showLoading()
        _viewState.addSource(createPagedList(posterSize, backdropSize, section)) { pagedList ->
            if (pagedList.isNotEmpty()) {
                _viewState.value = MovieListViewState.showMovieList(pagedList)
            }
        }
    }

    /**
     * Creates a [LiveData] object of the [PagedList] that is used to wire up the Android Paging Library
     * with the interactor in order to fetch a new page of movies each time the user scrolls down in
     * the list of movies.
     */
    private fun createPagedList(posterSize: Int, backdropSize: Int, section: MovieSection): LiveData<PagedList<MovieListItem>> {
        return createPagingFactory(posterSize, backdropSize, section)
                .map { mapDomainMovie(it) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(2)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(CoroutineExecutor(viewModelScope, Dispatchers.IO))
                            .build()
                }
    }

    /**
     * Creates an instance of [MPPagingDataSourceFactory] that is used to retrieve new pages of movies
     * every time the user reaches the end of current page.
     *
     *
     * IMPORTANT:
     * The lambda created as parameter of the factory executes it work in a background thread.
     * It does two basic things in the background:
     *  1 - Produces a List of Movies from the [movieListInteractor].
     *  2 - Configures the images path of each Movie in the list with the [imagesPathInteractor].
     */
    private fun createPagingFactory(moviePosterSize: Int, movieBackdropSize: Int, section: MovieSection): MPPagingDataSourceFactory<Movie> {
        return MPPagingDataSourceFactory { page, callback ->
            movieListInteractor.fetchMoviePageForSection(page, section) { movieList ->
                callback(movieList.map { imagesPathInteractor.configurePathMovie(moviePosterSize, movieBackdropSize, it) })
            }
        }
    }

    /**
     * Asks the interactor to flush any data that might be locally cached and re-fetch the
     * movie list for the current section being shown.
     */
    private fun refreshData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                with(movieListInteractor) {
                    flushMoviePagesForSection(MovieSection.Playing)
                    flushMoviePagesForSection(MovieSection.Popular)
                    flushMoviePagesForSection(MovieSection.Upcoming)
                    flushMoviePagesForSection(MovieSection.TopRated)
                }
            }
            postLoadingAndInitializePagedList(
                    currentParam.posterSize,
                    currentParam.backdropSize,
                    currentParam.section
            )
        }
    }

    private fun mapDomainMovie(domainMovie: Movie) = with(domainMovie) {
        MovieListItem(
                movieId = id,
                headerImageUrl = backdrop_path ?: "emptyPath",
                title = title,
                contentImageUrl = poster_path ?: "emptyPath",
                popularity = popularity.toString(),
                voteCount = vote_count.toString()
        )
    }
}
