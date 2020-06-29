package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.MovieStateRepository
import com.jpp.mpdomain.repository.SessionRepository

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

    suspend fun execute(movieId: Double, asFavorite: Boolean): Try<Unit> {
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
        )

        return if (result) {
            moviePageRepository.flushFavoriteMoviePages()
            Try.Success(Unit)
        } else {
            Try.Failure(Try.FailureCause.Unknown)
        }
    }
}
