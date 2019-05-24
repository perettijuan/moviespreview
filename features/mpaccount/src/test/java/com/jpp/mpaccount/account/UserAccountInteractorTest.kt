package com.jpp.mpaccount.account

import com.jpp.mpaccount.account.UserAccountInteractor.UserAccountEvent
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class UserAccountInteractorTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @MockK
    private lateinit var accountRepository: AccountRepository
    @MockK
    private lateinit var sessionRepository: SessionRepository

    private lateinit var subject: UserAccountInteractor

    @BeforeEach
    fun setUp() {
        subject = UserAccountInteractor(
                connectivityRepository,
                sessionRepository,
                accountRepository
        )
    }

    @Test
    fun `Should post user not logged when no session available`() {
        var eventPosted: UserAccountEvent? = null

        every { sessionRepository.getCurrentSession() } returns null

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchUserAccountData()

        assertEquals(UserAccountEvent.UserNotLogged, eventPosted)
    }

    @Test
    fun `Should post not connected event when user logged in but no connected to network`() {
        var eventPosted: UserAccountEvent? = null

        every { sessionRepository.getCurrentSession() } returns mockk()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchUserAccountData()

        assertEquals(UserAccountEvent.NotConnectedToNetwork, eventPosted)
    }

    @Test
    fun `Should post unknown error event when user logged and connected to network but fails to fetch data`() {
        var eventPosted: UserAccountEvent? = null

        every { sessionRepository.getCurrentSession() } returns mockk()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { accountRepository.getUserAccount(any()) } returns null

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchUserAccountData()

        assertEquals(UserAccountEvent.UnknownError, eventPosted)
    }

    @Test
    fun `Should post success event when user logged and connected to network and can fetch user account data`() {
        var eventPosted: UserAccountEvent? = null
        val session = mockk<Session>()
        val accountData = mockk<UserAccount>()

        every { sessionRepository.getCurrentSession() } returns session
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { accountRepository.getUserAccount(any()) } returns accountData

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchUserAccountData()

        assertEquals(UserAccountEvent.Success(accountData), eventPosted)
        verify { accountRepository.getUserAccount(session) }
    }
}