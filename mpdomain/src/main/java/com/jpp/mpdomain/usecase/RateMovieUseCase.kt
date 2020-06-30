package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.MovieStateRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Use case to rate a specific movie.
 */
class RateMovieUseCase(
    private val sessionRepository: SessionRepository,
    private val moviePageRepository: MoviePageRepository,
    private val movieStateRepository: MovieStateRepository,
    private val accountRepository: AccountRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(movieId: Double, rating: Float): Try<Unit> {
        if (connectivityRepository.getCurrentConnectivity() is Connectivity.Disconnected) {
            return Try.Failure(Try.FailureCause.NoConnectivity)
        }

        val currentSession = sessionRepository.getCurrentSession()
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        val userAccount = accountRepository.getUserAccount(currentSession)
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        val success = movieStateRepository.rateMovie(
            movieId, rating, userAccount, currentSession
        )

        return if (success) {
            moviePageRepository.flushRatedMoviePages()
            Try.Success(Unit)
        } else {
            Try.Failure(Try.FailureCause.Unknown)
        }
    }
}
