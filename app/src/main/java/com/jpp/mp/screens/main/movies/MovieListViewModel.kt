package com.jpp.mp.screens.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.CoroutineExecutor
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.paging.MPPagingDataSourceFactory
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import javax.inject.Inject
import com.jpp.mp.screens.main.movies.MovieListInteractor.MovieListEvent.*

/**
 * ViewModel used to support the movie list section of the application.
 */
class MovieListViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                             private val movieListInteractor: MovieListInteractor,
                                             private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {

    private val _viewStates = MediatorLiveData<HandledViewState<MovieListViewState>>()
    val viewStates: LiveData<HandledViewState<MovieListViewState>> get() = _viewStates

    private val _screenTitle = MutableLiveData<MovieListSectionTitle>()
    val screenTitle: LiveData<MovieListSectionTitle> get() = _screenTitle

    private val _navEvents = SingleLiveEvent<MoviesViewNavigationEvent>()
    val navEvents: LiveData<MoviesViewNavigationEvent> get() = _navEvents

    private lateinit var dsFactoryCreator: (() -> MPPagingDataSourceFactory<Movie>)
    private val retry: () -> Unit = { pushLoadingAndInitializePagedList(dsFactoryCreator) }

    init {
        _viewStates.addSource(movieListInteractor.events) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(MovieListViewState.showNoConnectivityError(retry))
                is UnknownError -> _viewStates.value = of(MovieListViewState.showUnknownError(retry))
                //TODO JPP UserChangedLanguage
            }
        }
    }

    /**
     * Called when the playing movies section is initialized.
     */
    fun onInitWithPlayingSection(posterSize: Int, backdropSize: Int) {
        dsFactoryCreator = {
            createPagingFactory(posterSize, backdropSize) { page, callback ->
                movieListInteractor.fetchPlayingMoviePage(page, callback)
            }
        }
        pushLoadingAndInitializePagedList(dsFactoryCreator)
        _screenTitle.postValue(MovieListSectionTitle.PLAYING)
    }

    /**
     * Called when the popular movies section is initialized.
     */
    fun onInitWithPopularSection(posterSize: Int, backdropSize: Int) {
        dsFactoryCreator = {
            createPagingFactory(posterSize, backdropSize) { page, callback ->
                movieListInteractor.fetchPopularMoviePage(page, callback)
            }
        }
        pushLoadingAndInitializePagedList(dsFactoryCreator)
        _screenTitle.postValue(MovieListSectionTitle.POPULAR)
    }

    /**
     * Called when the top rated movies section is initialized.
     */
    fun onInitWithTopRatedSection(posterSize: Int, backdropSize: Int) {
        dsFactoryCreator = {
            createPagingFactory(posterSize, backdropSize) { page, callback ->
                movieListInteractor.fetchTopRatedMoviePage(page, callback)
            }
        }
        pushLoadingAndInitializePagedList(dsFactoryCreator)
        _screenTitle.postValue(MovieListSectionTitle.TOP_RATED)
    }

    /**
     * Called when the upcmoing movies section is initialized.
     */
    fun onInitWithUpcomingSection(posterSize: Int, backdropSize: Int) {
        dsFactoryCreator = {
            createPagingFactory(posterSize, backdropSize) { page, callback ->
                movieListInteractor.fetchUpcomingMoviePage(page, callback)
            }
        }
        pushLoadingAndInitializePagedList(dsFactoryCreator)
        _screenTitle.postValue(MovieListSectionTitle.UPCOMING)
    }



    /**
     * Pushes the Loading view state into the view layer and creates the [PagedList]
     * of [MovieItem] that will be rendered by the view layer.
     */
    private fun pushLoadingAndInitializePagedList(dataSourceFactoryCreator: () -> MPPagingDataSourceFactory<Movie>) {
        with(_viewStates) {
            value = of(MovieListViewState.showLoading())
            addSource(createPagedList(dataSourceFactoryCreator)) { pagedList ->
                if (pagedList.isNotEmpty()) {
                    value = of(MovieListViewState.showMovieList(pagedList))
                }
            }
        }
    }

    /**
     * Creates a [LiveData] object of the [PagedList] that is used to wire up the Android Paging Library
     * with the interactor in order to fetch a new page of movies each time the user scrolls down in
     * the list of movies.
     * [dataSourceFactoryCreator] is a factory method that provides a mechanism used to instantiate
     * the proper [MPPagingDataSourceFactory] instance based on the movies fetching strategy required
     * for the section being shown to the user. Check the documentation in [createPagingFactory] in
     * order to fully understand how this behaves.
     */
    private fun createPagedList(dataSourceFactoryCreator: () -> MPPagingDataSourceFactory<Movie>): LiveData<PagedList<MovieItem>> {
        return dataSourceFactoryCreator()
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
     * every time the user reaches the end of current page being used. It is basically a method to
     * support the usage of the Android Paging Library.
     * [fetchStrategy] provides a mechanism to execute the proper method in the [MovieListInteractor]
     * to fetch the movies for the section being shown, since this VM supports 4 different types of
     * movie sections (Playing, Popular, TopRated and Upcoming).
     *
     *
     * IMPORTANT:
     * The lambda created as parameter of the factory executes it work in a background thread.
     * It does two basic things in the background:
     *  1 - Produces a List of Movies from the [movieListInteractor].
     *  2 - Configures the images path of each Movie in the list with the [imagesPathInteractor].
     */
    private fun createPagingFactory(moviePosterSize: Int, movieBackdropSize: Int, fetchStrategy: (Int, (List<Movie>) -> Unit) -> Unit): MPPagingDataSourceFactory<Movie> {
        return MPPagingDataSourceFactory { page, callback ->
            fetchStrategy(page) { movieList ->
                callback(movieList.map { imagesPathInteractor.configurePathMovie(moviePosterSize, movieBackdropSize, it) })
            }
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