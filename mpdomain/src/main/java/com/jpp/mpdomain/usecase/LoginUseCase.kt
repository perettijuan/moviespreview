package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Use case to perform a login.
 */
class LoginUseCase(
    private val sessionRepository: SessionRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(accessToken: AccessToken): Try<Unit> {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> Try.Failure(Try.FailureCause.NoConnectivity)
            is Connectivity.Connected -> sessionRepository.createSession(accessToken)?.let {
                Try.Success(Unit)
            } ?: Try.Failure(Try.FailureCause.Unknown)
        }
    }
}
