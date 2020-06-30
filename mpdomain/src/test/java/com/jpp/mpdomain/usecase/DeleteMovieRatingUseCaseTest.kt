package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.MovieStateRepository
import com.jpp.mpdomain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
class DeleteMovieRatingUseCaseTest {

    @MockK
    private lateinit var movieStateRepository: MovieStateRepository

    @RelaxedMockK
    private lateinit var moviePageRepository: MoviePageRepository

    @MockK
    private lateinit var sessionRepository: SessionRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: DeleteMovieRatingUseCase

    @BeforeEach
    fun setUp() {
        subject = DeleteMovieRatingUseCase(
            sessionRepository,
            moviePageRepository,
            movieStateRepository,
            connectivityRepository
        )
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with user not logged`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns null

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns mockk()
        coEvery {
            movieStateRepository.deleteMovieRate(
                any(),
                any()
            )
        } returns false

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should delete movie rating and flush favorites from page`() = runBlocking {
        val movieId = 1.toDouble()
        val session = mockk<Session>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns session
        coEvery {
            movieStateRepository.deleteMovieRate(
                movieId,
                session
            )
        } returns true

        val actual = subject.execute(movieId)

        assertTrue(actual is Try.Success)

        coVerify { moviePageRepository.flushRatedMoviePages() }
    }
}
