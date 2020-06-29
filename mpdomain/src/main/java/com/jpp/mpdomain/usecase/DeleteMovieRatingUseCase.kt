package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.MovieStateRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Deletes the rating set on a specific movie.
 */
class DeleteMovieRatingUseCase(
    private val sessionRepository: SessionRepository,
    private val moviePageRepository: MoviePageRepository,
    private val movieStateRepository: MovieStateRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(movieId: Double): Try<Unit> {
        if (connectivityRepository.getCurrentConnectivity() is Connectivity.Disconnected) {
            return Try.Failure(Try.FailureCause.NoConnectivity)
        }

        val currentSession = sessionRepository.getCurrentSession()
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        val success = movieStateRepository.deleteMovieRate(
            movieId, currentSession
        )

        return if (success) {
            moviePageRepository.flushRatedMoviePages()
            Try.Success(Unit)
        } else {
            Try.Failure(Try.FailureCause.Unknown)
        }
    }
}
