package com.jpp.mp.screens.main.userlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import com.jpp.mpdomain.usecase.support.RefreshAppDataUseCase
import java.util.concurrent.Executor
import javax.inject.Inject

/**
 * [ViewModel] to support the different user movies sections in the account user flow.
 *
 * - Exposes an output in a LiveData object that receives [UserMoviesViewState] updates as soon
 * as any new state is identified by the ViewModel.
 * - Exposes a second output in a LiveData object that receives [UserMoviesViewNavigationEvent] updates
 * as soon as a new navigation event is detected from the UI.
 */
class UserMoviesViewModel @Inject constructor(private val configMovieUseCase: ConfigMovieUseCase,
                                              private val refreshAppDataUseCase: RefreshAppDataUseCase,
                                              private val networkExecutor: Executor)
    : ViewModel() {

    private val viewState = MediatorLiveData<UserMoviesViewState>()
    private val navigationEvents by lazy { SingleLiveEvent<UserMoviesViewNavigationEvent>() }
    private lateinit var retryFunc: (() -> Unit)

    /**
     * Called on initialization of the UserMoviesFragment.
     * Each time this method is called, a new movie list will be fetched from the use case
     * and posted to the viewState, unless a previous list has been fetched.
     */
    fun fetchData(moviePosterSize: Int, movieBackdropSize: Int) {
        if (viewState.value is UserMoviesViewState.Loading
                || viewState.value is UserMoviesViewState.InitialPageLoaded) {
            return
        }


        viewState.value = UserMoviesViewState.Loading
        fetchFreshPage(moviePosterSize, movieBackdropSize)
        observeDataRefresh { fetchFreshPage(moviePosterSize, movieBackdropSize) }
    }

    /**
     * Exposes a stream that is updated with a new [UserMoviesViewState]
     * each time that a new state is identified.
     */
    fun viewState(): LiveData<UserMoviesViewState> = viewState

    /**
     * Exposes the events that are triggered when a navigation event is detected.
     * We need a different LiveData here in order to avoid the problem of back navigation:
     * - The default LiveData object posts the last value every time a new observer starts observing.
     */
    fun navEvents(): LiveData<UserMoviesViewNavigationEvent> = navigationEvents

    /**
     * Attempts to execute the last movie fetching step that was executed. Typically called after an error
     * is detected.
     */
    fun retry() {
        retryFunc.invoke()
    }

    /**
     * Called when the user selects an item from the list being shown.
     */
    fun onItemSelected(item: UserMovieItem) {
        with(item) {
            navigationEvents.value = UserMoviesViewNavigationEvent.ToMovieDetails(
                    movieId = movieId.toString(),
                    movieImageUrl = contentImageUrl,
                    movieTitle = title
            )
        }
    }

    /**
     * Starts the process to create the the PagedList that will back the list of movies shown to the
     * user.
     * When the data retrieved from [createPagedList] is obtained, a new state is pushed to viewState().
     */
    private fun fetchFreshPage(moviePosterSize: Int,
                               movieBackdropSize: Int) {
    }


    /**
     * The data shown in the section supported by this VM needs to be refreshed if the backing data
     * changes for some reason.
     * The VM starts observing the data refresh UC and if the user movies are updated, triggers doOnRefresh.
     */
    private fun observeDataRefresh(doOnRefresh: () -> Unit) {
        viewState.removeSource(refreshAppDataUseCase.appDataUpdates())
        viewState.addSource(refreshAppDataUseCase.appDataUpdates()) { dataRefresh ->
            when (dataRefresh) {
                is RefreshAppDataUseCase.AppDataRefresh.UserAccountMovies -> {
                    viewState.postValue(UserMoviesViewState.Refreshing)
                    doOnRefresh()
                }
                is RefreshAppDataUseCase.AppDataRefresh.LanguageChanged -> {
                    viewState.postValue(UserMoviesViewState.Refreshing)
                    doOnRefresh()
                }
            }
        }
    }

    private fun mapDomainMovie(domainMovie: Movie) = with(domainMovie) {
        UserMovieItem(
                movieId = id,
                headerImageUrl = backdrop_path ?: "emptyPath",
                title = title,
                contentImageUrl = poster_path ?: "emptyPath"
        )
    }

}