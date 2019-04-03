package com.jpp.mpdomain.usecase.details

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MovieAccountState
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MoviesRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpdomain.usecase.details.GetMovieAccountStateUseCase.MovieAccountStateResult.*
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
class GetMovieAccountStateUseCaseTest {

    @RelaxedMockK
    private lateinit var moviesRepository: MoviesRepository
    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @RelaxedMockK
    private lateinit var sessionRepository: SessionRepository

    private lateinit var subject: GetMovieAccountStateUseCase

    @BeforeEach
    fun setUp() {
        subject = GetMovieAccountStateUseCase.Impl(sessionRepository, moviesRepository, connectivityRepository)
    }

    @Test
    fun `Should check connectivity before fetching state and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.getMovieAccountState(1.toDouble()).let { result ->
            verify(exactly = 0) { sessionRepository.getCurrentSession() }
            verify(exactly = 0) { moviesRepository.getMovieAccountState(any(), any()) }
            assertEquals(ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return UserNotLogged when connected to network but user is not logged`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns null

        subject.getMovieAccountState(1.toDouble()).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 0) { moviesRepository.getMovieAccountState(any(), any()) }
            assertEquals(UserNotLogged, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        val session = mockk<Session>()
        val movieId = 1.toDouble()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { moviesRepository.getMovieAccountState(any(), any()) } returns null

        subject.getMovieAccountState(movieId).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { moviesRepository.getMovieAccountState(movieId, session) }
            assertEquals(ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected to network and can get account state`() {
        val session = mockk<Session>()
        val movieId = 1.toDouble()
        val accountState = mockk<MovieAccountState>()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { moviesRepository.getMovieAccountState(any(), any()) } returns accountState

        subject.getMovieAccountState(movieId).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { moviesRepository.getMovieAccountState(movieId, session) }
            assertTrue(result is Success)
            assertEquals(accountState, (result as Success).movieState)
        }
    }
}