package com.jpp.mp.main.header

import androidx.lifecycle.MutableLiveData
import com.jpp.mp.main.header.NavigationHeaderInteractor.HeaderDataEvent
import com.jpp.mp.main.header.NavigationHeaderInteractor.HeaderDataEvent.*
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class NavigationHeaderInteractorTest {


    @MockK
    private lateinit var sessionRepository: SessionRepository
    @MockK
    private lateinit var accountRepository: AccountRepository

    private val sessionUpdates = MutableLiveData<Session?>()
    private val accountUpdates = MutableLiveData<UserAccount>()

    private lateinit var subject: NavigationHeaderInteractor

    @BeforeEach
    fun setUp() {
        every { sessionRepository.sessionStateUpdates() } returns sessionUpdates
        every { accountRepository.userAccountUpdates() } returns accountUpdates

        subject = NavigationHeaderInteractor(sessionRepository, accountRepository)
    }

    @Test
    fun `Should post user not logged when session is removed`() {
        var eventPosted: HeaderDataEvent? = null

        subject.userAccountEvents.observeWith { eventPosted = it }

        sessionUpdates.postValue(null)

        assertEquals(UserNotLogged, eventPosted)
    }

    @Test
    fun `Should post user data when new data available`() {
        var eventPosted: HeaderDataEvent? = null
        val userData = mockk<UserAccount>()

        subject.userAccountEvents.observeWith { eventPosted = it }

        accountUpdates.postValue(userData)

        assertTrue(eventPosted is Success)
    }

    @Test
    fun `Should post user not logged when fetching user data without session`() {
        var eventPosted: HeaderDataEvent? = null

        every { sessionRepository.getCurrentSession() } returns null
        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.getUserAccountData()

        assertEquals(UserNotLogged, eventPosted)
    }

    @Test
    fun `Should post user data when fetching user data with session`() {
        var eventPosted: HeaderDataEvent? = null
        val userData = mockk<UserAccount>()
        val session = mockk<Session>()

        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns userData

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.getUserAccountData()

        assertTrue(eventPosted is Success)
        verify { accountRepository.getUserAccount(session) }
    }

    @Test
    fun `Should post unknown error when fetching user data with session is null`() {
        var eventPosted: HeaderDataEvent? = null
        val session = mockk<Session>()

        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns null

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.getUserAccountData()

        assertEquals(UnknownError, eventPosted)
    }
}