package com.jpp.mpdomain.usecase.account

import com.jpp.mpdomain.Connectivity.Connected
import com.jpp.mpdomain.Connectivity.Disconnected
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Defines a UseCase that retrieves the data of the user's account.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected and the user is logged in, retrieve the user account info.
 * If not connected, return an error that indicates such state.
 * If the user is not logged in, return an error that indicates such state.
 */
interface GetAccountInfoUseCase {

    /**
     * Defines all possible results of the use case execution.
     */
    sealed class AccountInfoResult {
        object UserNotLoggedIn : AccountInfoResult()
        object ErrorNoConnectivity : AccountInfoResult()
        object ErrorUnknown : AccountInfoResult()
        data class AccountInfo(val userAccount: UserAccount) : AccountInfoResult()
    }

    /**
     * Retrieves the user account information when the user is logged in and it is possible
     * to fetch the data.
     * @return
     *  - [AccountInfoResult.AccountInfo] when the user is logged in and the account can be retrieved.
     *  - [AccountInfoResult.UserNotLoggedIn] when the user is not logged in this device.
     *  - [AccountInfoResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [AccountInfoResult.ErrorUnknown] when an error occur while fetching the person.
     */
    fun getAccountInfo(): AccountInfoResult

    class Impl(private val sessionRepository: SessionRepository,
               private val accountRepository: AccountRepository,
               private val connectivityRepository: ConnectivityRepository) : GetAccountInfoUseCase {

        override fun getAccountInfo(): AccountInfoResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Disconnected -> AccountInfoResult.ErrorNoConnectivity
                Connected -> sessionRepository.getCurrentSession()?.let { session ->
                    accountRepository.getUserAccount(session)?.let {
                        AccountInfoResult.AccountInfo(mapAvatarUrl(it))
                    } ?: run {
                        AccountInfoResult.ErrorUnknown
                    }
                } ?: run {
                    AccountInfoResult.UserNotLoggedIn
                }
            }
        }

        private fun mapAvatarUrl(userAccount: UserAccount): UserAccount {
            return userAccount.copy(avatar = userAccount.avatar.copy(gravatar = userAccount.avatar.gravatar.copy(hash = Gravatar.BASE_URL + userAccount.avatar.gravatar.hash  + Gravatar.REDIRECT)))
        }
    }
}