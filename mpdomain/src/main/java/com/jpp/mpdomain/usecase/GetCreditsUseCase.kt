package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.CreditsRepository

/**
 * Use case to retrieve the [Credits] of a particular movie.
 */
class GetCreditsUseCase(
    private val connectivityRepository: ConnectivityRepository,
    private val creditsRepository: CreditsRepository
) {

    fun execute(movieId: Double): Try<Credits> {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> Try.Failure(Try.FailureCause.NoConnectivity)
            is Connectivity.Connected -> creditsRepository.getCreditsForMovie(movieId)
                ?.let { credits -> Try.Success(credits) } ?: Try.Failure(Try.FailureCause.Unknown)
        }
    }
}