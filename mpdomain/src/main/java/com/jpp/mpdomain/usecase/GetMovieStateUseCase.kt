package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MovieStateRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Use case to retrieve a [MovieState].
 */
class GetMovieStateUseCase(
    private val sessionRepository: SessionRepository,
    private val movieStateRepository: MovieStateRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(movieId: Double): Try<MovieState> {
        if (connectivityRepository.getCurrentConnectivity() is Connectivity.Disconnected) {
            return Try.Failure(Try.FailureCause.NoConnectivity)
        }

        val currentSession = sessionRepository.getCurrentSession()
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        return movieStateRepository.getStateForMovie(
            movieId, currentSession
        )?.let { movieState ->
            Try.Success(movieState)
        } ?: Try.Failure(Try.FailureCause.Unknown)
    }
}
