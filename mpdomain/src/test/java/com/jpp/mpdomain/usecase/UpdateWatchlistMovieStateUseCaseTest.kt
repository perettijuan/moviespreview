package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.MovieStateRepository
import com.jpp.mpdomain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UpdateWatchlistMovieStateUseCaseTest {

    @MockK
    private lateinit var movieStateRepository: MovieStateRepository

    @RelaxedMockK
    private lateinit var moviePageRepository: MoviePageRepository

    @MockK
    private lateinit var sessionRepository: SessionRepository

    @MockK
    private lateinit var accountRepository: AccountRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: UpdateWatchlistMovieStateUseCase

    @BeforeEach
    fun setUp() {
        subject = UpdateWatchlistMovieStateUseCase(
            movieStateRepository,
            moviePageRepository,
            sessionRepository,
            accountRepository,
            connectivityRepository
        )
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        coEvery { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute(1.0, true)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with user not logged`() = runBlocking {
        coEvery { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns null

        val actual = subject.execute(1.0, true)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with user not logged when no account available`() = runBlocking {
        coEvery { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns mockk()
        coEvery { accountRepository.getUserAccount(any()) } returns null

        val actual = subject.execute(1.0, true)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason`() = runBlocking {
        coEvery { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns mockk()
        coEvery { accountRepository.getUserAccount(any()) } returns mockk()
        coEvery {
            movieStateRepository.updateWatchlistMovieState(
                any(),
                any(),
                any(),
                any()
            )
        } returns false

        val actual = subject.execute(1.0, true)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should update watchlist movie state and flush watchlist from page`() = runBlocking {
        val movieId = 1.toDouble()
        val inWatchlist = true
        val session = mockk<Session>()
        val userAccount = mockk<UserAccount>()

        coEvery { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns session
        coEvery { accountRepository.getUserAccount(session) } returns userAccount
        coEvery {
            movieStateRepository.updateWatchlistMovieState(
                movieId,
                inWatchlist,
                userAccount,
                session
            )
        } returns true

        val actual = subject.execute(1.0, true)

        assertTrue(actual is Try.Success)

        coVerify { moviePageRepository.flushWatchlistMoviePages() }
    }
}
