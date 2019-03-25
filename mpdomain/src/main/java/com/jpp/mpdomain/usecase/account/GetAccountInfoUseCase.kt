package com.jpp.mpdomain.usecase.account

import com.jpp.mpdomain.repository.SessionRepository

/**
 * Defines a UseCase that retrieves the data of the user's account.
 */
interface GetAccountInfoUseCase {

    sealed class AccountInfoResult {
        object UserNotLoggedIn : AccountInfoResult()
        object AccountInfoAvailable : AccountInfoResult() // TODO add data to this object
    }

    fun getAccountInfo(): AccountInfoResult

    class Impl(private val sessionRepository: SessionRepository) : GetAccountInfoUseCase {
        override fun getAccountInfo(): AccountInfoResult {
            return sessionRepository.getSessionId()?.let {
                AccountInfoResult.AccountInfoAvailable
            } ?: run {
                AccountInfoResult.UserNotLoggedIn
            }
        }
    }
}