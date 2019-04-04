package com.jpp.mpdomain.usecase.movies

import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MoviesRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpdomain.Connectivity.Connected
import com.jpp.mpdomain.Connectivity.Disconnected

/**
 * Defines a UseCase that marks a movie as favorite or not favorite.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected and the user is logged in, update the movie favorite state.
 * If not connected, return an error that indicates such state.
 */
interface MarkMovieAsFavoriteUseCase {

    sealed class FavoriteMovieResult {
        object ErrorNoConnectivity : FavoriteMovieResult()
        object ErrorUnknown : FavoriteMovieResult()
        object UserNotLogged : FavoriteMovieResult()
        object Success : FavoriteMovieResult()
    }

    /**
     * Marks the provided [movieId] as favorite (or not) depending on the value of [asFavorite].
     * @return
     *  - [FavoriteMovieResult.Success] when there is internet connectivity the movieId can be updated as favorite.
     *  - [FavoriteMovieResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [FavoriteMovieResult.ErrorUnknown] when an error occur while fetching the page.
     *  - [FavoriteMovieResult.UserNotLogged] when the user is not logged in the system.
     */
    fun favoriteMovie(movieId: Double, asFavorite: Boolean): FavoriteMovieResult

    class Impl(private val sessionRepository: SessionRepository,
               private val accountRepository: AccountRepository,
               private val moviesRepository: MoviesRepository,
               private val connectivityRepository: ConnectivityRepository) : MarkMovieAsFavoriteUseCase {

        override fun favoriteMovie(movieId: Double, asFavorite: Boolean): FavoriteMovieResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Disconnected -> FavoriteMovieResult.ErrorNoConnectivity
                Connected -> sessionRepository.getCurrentSession()?.let { session ->
                    accountRepository.getUserAccount(session)?.let { userAccount ->
                        when (moviesRepository.updateMovieFavoriteState(movieId, asFavorite, userAccount, session)) {
                            true -> FavoriteMovieResult.Success
                            false -> FavoriteMovieResult.ErrorUnknown
                        }
                    }
                } ?: run {
                    FavoriteMovieResult.UserNotLogged
                }
            }
        }
    }

}