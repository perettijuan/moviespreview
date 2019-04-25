package com.jpp.mpdomain.usecase.account

import com.jpp.mpdomain.Connectivity.Connected
import com.jpp.mpdomain.Connectivity.Disconnected
import com.jpp.mpdomain.MovieAccountState
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Defines a UseCase that retrieves the state of a particular movie from the user's account perspective.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected and the user is logged in, retrieve the state of the movie.
 * If not connected, return an error that indicates such state.
 */
interface GetMovieAccountStateUseCase {

    /**
     * Represents the result of a a movies fetching execution.
     */
    sealed class MovieAccountStateResult {
        object ErrorNoConnectivity : MovieAccountStateResult()
        object ErrorUnknown : MovieAccountStateResult()
        object UserNotLogged : MovieAccountStateResult()
        data class Success(val movieState: MovieAccountState) : MovieAccountStateResult()
    }

    /**
     * Retrieves the account state of the movie identified by [movieId].
     * @return
     *  - [MovieAccountStateResult.Success] when there is internet connectivity and the movie state can be retrieved.
     *  - [MovieAccountStateResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [MovieAccountStateResult.ErrorUnknown] when an error occur while fetching the page.
     *  - [MovieAccountStateResult.UserNotLogged] when the user is not logged in the system.
     */
    fun getMovieAccountState(movieId: Double): MovieAccountStateResult


    class Impl(private val sessionRepository: SessionRepository,
               private val accountRepository: AccountRepository,
               private val connectivityRepository: ConnectivityRepository) : GetMovieAccountStateUseCase {

        override fun getMovieAccountState(movieId: Double): MovieAccountStateResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Disconnected -> MovieAccountStateResult.ErrorNoConnectivity
                Connected -> sessionRepository.getCurrentSession()?.let {
                    accountRepository.getMovieAccountState(movieId, it)?.let { movieAccountState ->
                        MovieAccountStateResult.Success(movieAccountState)
                    } ?: run {
                        MovieAccountStateResult.ErrorUnknown
                    }
                } ?: run {
                    MovieAccountStateResult.UserNotLogged
                }
            }
        }
    }

}