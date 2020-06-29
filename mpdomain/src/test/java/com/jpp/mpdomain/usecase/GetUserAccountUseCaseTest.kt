package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetUserAccountUseCaseTest {

    @MockK
    private lateinit var sessionRepository: SessionRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    @MockK
    private lateinit var accountRepository: AccountRepository

    private lateinit var subject: GetUserAccountUseCase

    @BeforeEach
    fun setUp() {
        subject = GetUserAccountUseCase(
            accountRepository,
            sessionRepository,
            connectivityRepository
        )
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute()

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with user not logged`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns null

        val actual = subject.execute()

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns mockk()
        coEvery { accountRepository.getUserAccount(any()) } returns null

        val actual = subject.execute()

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should retrieve user account successfully`() = runBlocking {
        val currentSession: Session = mockk()
        val expected: UserAccount = mockk()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns currentSession
        coEvery { accountRepository.getUserAccount(currentSession) } returns expected

        val actual = subject.execute()

        assertTrue(actual is Try.Success)
        assertEquals(expected, actual.getOrNull())
    }
}
