package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.AccessTokenRepository
import com.jpp.mpdomain.repository.ConnectivityRepository

/**
 * Use case to retrieve an [AccessToken].
 */
class GetAccessTokenUseCase(
    private val accessTokenRepository: AccessTokenRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(): Try<AccessToken> {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> Try.Failure(Try.FailureCause.NoConnectivity)
            is Connectivity.Connected -> accessTokenRepository.getAccessToken()
                ?.let { accessToken ->
                    Try.Success(accessToken)
                } ?: Try.Failure(Try.FailureCause.Unknown)
        }
    }
}
