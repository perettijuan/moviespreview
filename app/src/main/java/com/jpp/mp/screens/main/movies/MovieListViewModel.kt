package com.jpp.mp.screens.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.CoroutineExecutor
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.paging.MPPagingDataSourceFactory
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mp.screens.main.movies.MovieListInteractor.MovieListEvent.*
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel used to support the movie list section of the application. This ViewModel is shared by
 * the Fragments that show the movies listed in each category that can be displayed. Every time the
 * user selects a section, this VM is refreshed and triggers a new fetching to the underlying layers
 * of the application.
 * Produces different [MoviesViewState] that represents the entire configuration of the screen at any
 * given moment.
 *
 * This the UI is using the Android Paging Library, the VM needs a way to map the data retrieved from
 * the [MovieListInteractor] to a [PagedList] that can be used by the library. That process is done
 * using the [MPPagingDataSourceFactory] that creates the DataSource and produces a [LiveData] object
 * that is combined with the [viewStates] in order to properly map the data into a [MoviesViewState].
 *
 * This VM is also language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class MovieListViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                             private val movieListInteractor: MovieListInteractor,
                                             private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {

    private val _viewStates = MediatorLiveData<HandledViewState<MovieListViewState>>()
    val viewStates: LiveData<HandledViewState<MovieListViewState>> get() = _viewStates

    private val _navEvents = SingleLiveEvent<MoviesViewNavigationEvent>()
    val navEvents: LiveData<MoviesViewNavigationEvent> get() = _navEvents

    private lateinit var retry: () -> Unit
    private lateinit var sectionTitle: MovieListSectionTitle

    init {
        _viewStates.addSource(movieListInteractor.events) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(MovieListViewState.showNoConnectivityError(sectionTitle, retry))
                is UnknownError -> _viewStates.value = of(MovieListViewState.showUnknownError(sectionTitle, retry))
                is UserChangedLanguage -> refreshData()
            }
        }
    }

    /**
     * Called when the playing movies section is initialized.
     */
    fun onInitWithPlayingSection(posterSize: Int, backdropSize: Int) {
        pushLoadingAndInitializePagedList(posterSize, backdropSize, MovieSection.Playing)
    }

    /**
     * Called when the popular movies section is initialized.
     */
    fun onInitWithPopularSection(posterSize: Int, backdropSize: Int) {
        pushLoadingAndInitializePagedList(posterSize, backdropSize, MovieSection.Popular)
    }

    /**
     * Called when the top rated movies section is initialized.
     */
    fun onInitWithTopRatedSection(posterSize: Int, backdropSize: Int) {
        pushLoadingAndInitializePagedList(posterSize, backdropSize, MovieSection.TopRated)
    }

    /**
     * Called when the upcoming movies section is initialized.
     */
    fun onInitWithUpcomingSection(posterSize: Int, backdropSize: Int) {
        pushLoadingAndInitializePagedList(posterSize, backdropSize, MovieSection.Upcoming)
    }

    /**
     * Called when an item is selected in the list of movies.
     * A new state is posted in navEvents() in order to handle the event.
     */
    fun onMovieSelected(movieItem: MovieItem, positionInList: Int) {
        with(movieItem) {
            _navEvents.value = MoviesViewNavigationEvent.ToMovieDetails(
                    movieId = movieId.toString(),
                    movieImageUrl = contentImageUrl,
                    movieTitle = title,
                    positionInList = positionInList)
        }
    }


    /**
     * Pushes the Loading view state into the view layer and creates the [PagedList]
     * of [MovieItem] that will be rendered by the view layer.
     */
    private fun pushLoadingAndInitializePagedList(posterSize: Int, backdropSize: Int, section: MovieSection) {
        sectionTitle = when (section) {
            MovieSection.Playing -> MovieListSectionTitle.PLAYING
            MovieSection.Popular -> MovieListSectionTitle.POPULAR
            MovieSection.TopRated -> MovieListSectionTitle.TOP_RATED
            MovieSection.Upcoming -> MovieListSectionTitle.UPCOMING
        }

        //On every retry, we want to show the loading screen and then fetch the data and show it.
        retry = {
            _viewStates.value = of(MovieListViewState.showLoading(sectionTitle))
            _viewStates.addSource(createPagedList(posterSize, backdropSize, section)) { pagedList ->
                if (pagedList.isNotEmpty()) {
                    _viewStates.value = of(MovieListViewState.showMovieList(sectionTitle, pagedList))
                }
            }
        }.also {
            // invoke the function in order to actually push the loading state and fetch the data when this method is called.
            it.invoke()
        }
    }

    /**
     * Creates a [LiveData] object of the [PagedList] that is used to wire up the Android Paging Library
     * with the interactor in order to fetch a new page of movies each time the user scrolls down in
     * the list of movies.
     */
    private fun createPagedList(posterSize: Int, backdropSize: Int, section: MovieSection): LiveData<PagedList<MovieItem>> {
        return createPagingFactory(posterSize, backdropSize, section)
                .map { mapDomainMovie(it) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(2)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(CoroutineExecutor(this, dispatchers.default()))
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
     * Ask the interactor to flush any data that might be locally cached and re-fetch the
     * movie list for the current section being shown.
     */
    private fun refreshData() {
        launch {
            withContext(dispatchers.default()) {
                with(movieListInteractor) {
                    flushMoviePagesForSection(MovieSection.Playing)
                    flushMoviePagesForSection(MovieSection.Popular)
                    flushMoviePagesForSection(MovieSection.Upcoming)
                    flushMoviePagesForSection(MovieSection.TopRated)
                }
            }
            retry.invoke()
        }
    }

    private fun mapDomainMovie(domainMovie: Movie) = with(domainMovie) {
        MovieItem(
                movieId = id,
                headerImageUrl = backdrop_path ?: "emptyPath",
                title = title,
                contentImageUrl = poster_path ?: "emptyPath",
                popularity = popularity.toString(),
                voteCount = vote_count.toString()
        )
    }
}