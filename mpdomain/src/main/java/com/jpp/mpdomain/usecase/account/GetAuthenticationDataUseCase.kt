package com.jpp.mpdomain.usecase.account

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity.*
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Defines a UseCase that retrieves the data needed to authenticate the user.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected, retrieve a new access token and prepare the URLs to perform the
 * Oauth process.
 * If not connected, return an error that indicates such state.
 */
interface GetAuthenticationDataUseCase {

    /**
     * Defines all possible results of the use case execution.
     */
    sealed class AuthenticationDataResult {
        object ErrorNoConnectivity : AuthenticationDataResult()
        object ErrorUnknown : AuthenticationDataResult()
        data class Success(val authenticationURL: String, val redirectionUrl: String, val accessToken: AccessToken) : AuthenticationDataResult()
    }

    /**
     * Retrieves an [AccessToken] to be used in the user authentication flow and creates the URLs needed
     * to perform the Oauth process.
     * @return
     *  - [AuthenticationDataResult.Success] when the access token has been retrieved successfully.
     *  - [AuthenticationDataResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [AuthenticationDataResult.ErrorUnknown] when an error occur while fetching the person.
     */
    fun getAuthenticationData(): AuthenticationDataResult

    class Impl(private val sessionRepository: SessionRepository,
               private val connectivityRepository: ConnectivityRepository) : GetAuthenticationDataUseCase {

        override fun getAuthenticationData(): AuthenticationDataResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Disconnected -> AuthenticationDataResult.ErrorNoConnectivity
                Connected -> sessionRepository.getAccessToken()?.let { at ->
                    when (at.success) {
                        true -> AuthenticationDataResult.Success(
                                authenticationURL = sessionRepository.getAuthenticationUrl(at.request_token),
                                redirectionUrl = sessionRepository.getAuthenticationRedirection(),
                                accessToken = at)
                        else -> AuthenticationDataResult.ErrorUnknown
                    }
                } ?: run {
                    AuthenticationDataResult.ErrorUnknown
                }
            }
        }
    }
}