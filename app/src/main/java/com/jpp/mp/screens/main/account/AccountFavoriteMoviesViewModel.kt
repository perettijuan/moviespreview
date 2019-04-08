package com.jpp.mp.screens.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.screens.CoroutineDispatchers
import com.jpp.mp.screens.MPScopedViewModel
import com.jpp.mpdomain.usecase.account.GetFavoriteMoviesUseCase
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.jpp.mpdomain.usecase.account.GetFavoriteMoviesUseCase.FavoriteMoviesResult.*
import com.jpp.mp.screens.main.account.FavoriteMoviesViewState.*

/**
 * TODO JPP add description here
 */
class AccountFavoriteMoviesViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                         private val favoritesMoviesUseCase: GetFavoriteMoviesUseCase,
                                                         private val configMovieUseCase: ConfigMovieUseCase)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<FavoriteMoviesViewState>() }

    fun init(imageTargetSize: Int) {
        viewStateLiveData.value = FavoriteMoviesViewState.Loading
        launch {
            viewStateLiveData.value = fetchFavoriteMovies(imageTargetSize)
        }
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [FavoriteMoviesViewState].
     */
    fun viewState(): LiveData<FavoriteMoviesViewState> = viewStateLiveData

    private suspend fun fetchFavoriteMovies(imageTargetSize: Int): FavoriteMoviesViewState = withContext(dispatchers.default()) {
        favoritesMoviesUseCase
                .getUserFavoriteMovies(0)
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
                                        FavoriteMovie(title = it.title, posterPath = it.poster_path ?: "emptyPath")
                                    }
                                    .let {
                                        FavoriteMovies(it)
                                    }
                        }
                    }
                }
    }
}