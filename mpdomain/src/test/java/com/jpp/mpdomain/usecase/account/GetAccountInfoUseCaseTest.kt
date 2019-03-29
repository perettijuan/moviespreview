package com.jpp.mpdomain.usecase.account

import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase.AccountInfoResult.*
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetAccountInfoUseCaseTest {

    @RelaxedMockK
    private lateinit var sessionRepository: SessionRepository

    @RelaxedMockK
    private lateinit var accountRepository: AccountRepository

    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: GetAccountInfoUseCase

    @BeforeEach
    fun setUp() {
        subject = GetAccountInfoUseCase.Impl(sessionRepository, accountRepository, connectivityRepository)
    }

    @Test
    fun `Should check connectivity before attempting to fetch user account and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.getAccountInfo().let { result ->
            verify(exactly = 0) { sessionRepository.getCurrentSession() }
            verify(exactly = 0) { accountRepository.getUserAccount(any()) }
            assertEquals(ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return UserNotLoggedIn when connected to network and the user is not logged in`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns null

        subject.getAccountInfo().let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 0) { accountRepository.getUserAccount(any()) }
            assertEquals(UserNotLoggedIn, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and the user is logged but an error is detected`() {
        val session = mockk<Session>()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns null

        subject.getAccountInfo().let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { accountRepository.getUserAccount(session) }
            assertEquals(ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return AccountInfo when connected to network and the user is logged`() {
        val session = mockk<Session>()
        val account = mockk<UserAccount>(relaxed = true)
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns account

        subject.getAccountInfo().let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { accountRepository.getUserAccount(session) }
            assertTrue(result is AccountInfo)
        }
    }

    @Test
    fun `Should map avatar info in UserAccount`() {
        val hash = "hashString"
        val gravatar = Gravatar(hash)
        val userAvatar = UserAvatar(gravatar)
        val account = UserAccount(avatar = userAvatar, name = "", username = "", id = 12.toDouble())

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns mockk()
        every { accountRepository.getUserAccount(any()) } returns account

        subject.getAccountInfo().let { result ->
            result as AccountInfo
            assertEquals(Gravatar.BASE_URL + hash, result.userAccount.avatar.gravatar.hash)
        }
    }
}