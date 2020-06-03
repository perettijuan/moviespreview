package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.*

/**
 * Use case to update the favorite state of a movie.
 */
class UpdateFavoriteMovieStateUseCase(
    private val movieStateRepository: MovieStateRepository,
    private val moviePageRepository: MoviePageRepository,
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(movieId: Double, asFavorite: Boolean): Try<Boolean> {
        if (connectivityRepository.getCurrentConnectivity() is Connectivity.Disconnected) {
            return Try.Failure(Try.FailureCause.NoConnectivity)
        }

        val currentSession = sessionRepository.getCurrentSession()
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        val userAccount = accountRepository.getUserAccount(currentSession)
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        val result = movieStateRepository.updateFavoriteMovieState(
            movieId,
            asFavorite,
            userAccount,
            currentSession
        ).also {
            moviePageRepository.flushFavoriteMoviePages()
        }

        return if (result) {
            Try.Success(result)
        } else {
            Try.Failure(Try.FailureCause.Unknown)
        }
    }
}