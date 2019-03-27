package com.jpp.mpdomain.usecase.account

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpdomain.Connectivity.*

/**
 * Defines a UseCase that creates a new session to be used by the user in the application.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected, create a new session.
 * If not connected, return an error that indicates such state.
 */
interface CreateSessionUseCase {

    /**
     * Defines all possible results of the use case execution.
     */
    sealed class CreateSessionResult {
        object ErrorNoConnectivity : CreateSessionResult()
        object ErrorUnknown : CreateSessionResult()
        object Success : CreateSessionResult()
    }

    /**
     * Creates a new session from the [AccessToken] provided. The new session will be able to be used
     * to fetch new data and update data related to the user account.
     * @return
     *  - [CreateSessionResult.Success] when the new session is created.
     *  - [CreateSessionResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [CreateSessionResult.ErrorUnknown] when an error occur while fetching the person.
     */
    fun createSessionWith(accessToken: AccessToken): CreateSessionResult

    class Impl(private val sessionRepository: SessionRepository,
               private val connectivityRepository: ConnectivityRepository) : CreateSessionUseCase {

        override fun createSessionWith(accessToken: AccessToken): CreateSessionResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Disconnected -> CreateSessionResult.ErrorNoConnectivity
                Connected -> sessionRepository.getSession(accessToken)?.let { session ->
                    when (session.success) {
                        true -> CreateSessionResult.Success
                        false -> CreateSessionResult.ErrorUnknown
                    }
                } ?: run {
                    CreateSessionResult.ErrorUnknown
                }
            }
        }
    }

}