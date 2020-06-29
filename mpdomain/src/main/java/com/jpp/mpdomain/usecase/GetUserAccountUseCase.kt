package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Use case to retrieve the [UserAccount] data.
 */
class GetUserAccountUseCase(
    private val accountRepository: AccountRepository,
    private val sessionRepository: SessionRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(): Try<UserAccount> {
        if (connectivityRepository.getCurrentConnectivity() is Connectivity.Disconnected) {
            return Try.Failure(Try.FailureCause.NoConnectivity)
        }

        val currentSession = sessionRepository.getCurrentSession()
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        return accountRepository.getUserAccount(currentSession)?.let { userAccount ->
            Try.Success(userAccount)
        } ?: Try.Failure(Try.FailureCause.Unknown)
    }
}
