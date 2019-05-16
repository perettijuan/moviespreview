package com.jpp.mp.screens.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpdomain.usecase.account.GetFavoriteMoviesUseCase
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.jpp.mpdomain.usecase.account.GetFavoriteMoviesUseCase.FavoriteMoviesResult.*
import com.jpp.mp.screens.main.account.FavoriteMoviesViewState.*

/**
 * [MPScopedViewModel] to handle the state of the favorites section in AccountFragment. It is a coroutine-scoped
 * ViewModel, which indicates that some work will be executed in a background context and synced
 * to the main context when over.
 *
 * It exposes an output as a LiveData object that receives [FavoriteMoviesViewState] updates as soon
 * as any new state is identified by the ViewModel.
 */
class AccountFavoriteMoviesViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                         private val favoritesMoviesUseCase: GetFavoriteMoviesUseCase,
                                                         private val configMovieUseCase: ConfigMovieUseCase)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<FavoriteMoviesViewState>() }
    private lateinit var retryFunc: () -> Unit

    fun init(imageTargetSize: Int) {
        retryFunc = { pushLoadingAndFetchFavorites(imageTargetSize) }
        pushLoadingAndFetchFavorites(imageTargetSize)
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [FavoriteMoviesViewState].
     */
    fun viewState(): LiveData<FavoriteMoviesViewState> = viewStateLiveData


    /**
     * Called in order to execute the last attempt to fetch a movie detail.
     */
    fun retry() {
        when (viewStateLiveData.value) {
            is FavoriteMoviesViewState.UnableToLoad -> retryFunc.invoke()
        }
    }

    /**
     * Pushes the loading state into the UI and fetches the favorite movies of
     * the user.
     */
    private fun pushLoadingAndFetchFavorites(imageTargetSize: Int) {
        viewStateLiveData.value = FavoriteMoviesViewState.Loading
        launch {
            viewStateLiveData.value = fetchFavoriteMovies(imageTargetSize)
        }
    }

    /**
     * Fetches the favorite movies of the user - if it has - and prepares it to be
     * shown in the UI.
     * @return a [FavoriteMoviesViewState] that is posted in viewState in order
     * to update the UI.
     */
    private suspend fun fetchFavoriteMovies(imageTargetSize: Int): FavoriteMoviesViewState = withContext(dispatchers.default()) {
        favoritesMoviesUseCase
                .getUserFavoriteMovies(1)
                .let { ucResult ->
                    when (ucResult) {
                        is ErrorNoConnectivity -> UnableToLoad
                        is ErrorUnknown -> UnableToLoad
                        is UserNotLogged -> UnableToLoad
                        is NoFavorites -> NoFavoriteMovies
                        is Success -> {
                            ucResult.moviesPage.results
                                    .map {
                                        configMovieUseCase.configure(imageTargetSize, imageTargetSize, it).movie
                                    }
                                    .map {
                                        FavoriteMovie(posterPath = it.poster_path ?: "emptyPath")
                                    }
                                    .let {
                                        FavoriteMovies(it)
                                    }
                        }
                    }
                }
    }
}