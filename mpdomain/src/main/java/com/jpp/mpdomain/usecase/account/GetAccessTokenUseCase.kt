package com.jpp.mpdomain.usecase.account

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity.*
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Defines a UseCase that retrieves an access token to be used when authenticating the user.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected, retrieve a new access token.
 * If not connected, return an error that indicates such state.
 */
interface GetAccessTokenUseCase {

    /**
     * Defines all possible results of the use case execution.
     */
    sealed class AccessTokenResult {
        object ErrorNoConnectivity : AccessTokenResult()
        object ErrorUnknown : AccessTokenResult()
        data class Success(val accessToken: AccessToken) : AccessTokenResult()
    }

    /**
     * Retrieves an [AccessToken] to be used in the user authentication flow.
     * @return
     *  - [AccessTokenResult.Success] when the access token has been retrieved successfully.
     *  - [AccessTokenResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [AccessTokenResult.ErrorUnknown] when an error occur while fetching the person.
     */
    fun getAccessToken(): AccessTokenResult

    class Impl(private val sessionRepository: SessionRepository,
               private val connectivityRepository: ConnectivityRepository) : GetAccessTokenUseCase {
        override fun getAccessToken(): AccessTokenResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Disconnected -> AccessTokenResult.ErrorNoConnectivity
                Connected -> sessionRepository.getAccessToken()?.let { at ->
                    when (at.success) {
                        true -> AccessTokenResult.Success(at)
                        else -> AccessTokenResult.ErrorUnknown
                    }
                } ?: run {
                    AccessTokenResult.ErrorUnknown
                }
            }
        }
    }
}