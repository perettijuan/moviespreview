package com.jpp.mpdomain.usecase.account

import com.jpp.mpdomain.Connectivity.Connected
import com.jpp.mpdomain.Connectivity.Disconnected
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Defines a UseCase that retrieves the list of favorite movies of the user logged in  the application.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected and the user is logged in, retrieve the list of favorite movies.
 * If not connected, return an error that indicates such state.
 * If the user has no favorite movies, returns a state that represents the situation.
 */
interface GetFavoriteMoviesUseCase {

    /**
     * Represents the result the use case execution.
     */
    sealed class FavoriteMoviesResult {
        object ErrorNoConnectivity : FavoriteMoviesResult()
        object ErrorUnknown : FavoriteMoviesResult()
        object UserNotLogged : FavoriteMoviesResult()
        object NoFavorites : FavoriteMoviesResult()
        data class Success(val moviesPage: MoviePage) : FavoriteMoviesResult()
    }

    /**
     * Retrieves the list of movies that the
     * @return
     *  - [FavoriteMoviesResult.Success] when there is internet connectivity and the user has favorites movies.
     *  - [FavoriteMoviesResult.NoFavorites] when the user does not have favorite movies.
     *  - [FavoriteMoviesResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [FavoriteMoviesResult.ErrorUnknown] when an error occur while fetching the page.
     *  - [FavoriteMoviesResult.UserNotLogged] when the user is not logged in the system.
     */
    fun getUserFavoriteMovies(page: Int): FavoriteMoviesResult

    class Impl(private val sessionRepository: SessionRepository,
               private val accountRepository: AccountRepository,
               private val languageRepository: LanguageRepository,
               private val connectivityRepository: ConnectivityRepository) : GetFavoriteMoviesUseCase {

        override fun getUserFavoriteMovies(page: Int): FavoriteMoviesResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Disconnected -> FavoriteMoviesResult.ErrorNoConnectivity
                Connected -> sessionRepository.getCurrentSession()?.let { session ->
                    accountRepository.getUserAccount(session)?.let { userAccount ->
                        accountRepository.getFavoriteMovies(page, userAccount, session, languageRepository.getCurrentDeviceLanguage())?.let { page ->
                            when (page.results.size) {
                                0 -> FavoriteMoviesResult.NoFavorites
                                else -> FavoriteMoviesResult.Success(page)
                            }
                        } ?: run {
                            FavoriteMoviesResult.ErrorUnknown
                        }
                    }
                } ?: run {
                    FavoriteMoviesResult.UserNotLogged
                }
            }
        }
    }

}