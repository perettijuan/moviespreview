package com.jpp.mpdomain.usecase.movies

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MoviesRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpdomain.usecase.movies.MarkMovieAsFavoriteUseCase.FavoriteMovieResult.*
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MarkMovieAsFavoriteUseCaseTest {

    @RelaxedMockK
    private lateinit var sessionRepository: SessionRepository

    @RelaxedMockK
    private lateinit var accountRepository: AccountRepository

    @RelaxedMockK
    private lateinit var moviesRepository: MoviesRepository

    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: MarkMovieAsFavoriteUseCase

    @BeforeEach
    fun setUp() {
        subject = MarkMovieAsFavoriteUseCase.Impl(
                sessionRepository,
                accountRepository,
                moviesRepository,
                connectivityRepository
        )
    }

    @Test
    fun `Should check connectivity before attempting to fetch user account and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.favoriteMovie(movie, true).let { result ->
            verify(exactly = 0) { sessionRepository.getCurrentSession() }
            verify(exactly = 0) { accountRepository.getUserAccount(any()) }
            verify(exactly = 0) { moviesRepository.updateMovieFavoriteState(any(), any(), any(), any()) }
            assertEquals(ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return UserNotLoggedIn when connected to network and the user is not logged in`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns null

        subject.favoriteMovie(movie, true).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 0) { accountRepository.getUserAccount(any()) }
            verify(exactly = 0) { moviesRepository.updateMovieFavoriteState(any(), any(), any(), any()) }
            assertEquals(UserNotLogged, result)
        }
    }

    @Test
    fun `Should return UserNotLoggedIn when connected to network and there is no user account data`() {
        val session = mockk<Session>()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns null

        subject.favoriteMovie(movie, true).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { accountRepository.getUserAccount(session) }
            verify(exactly = 0) { moviesRepository.updateMovieFavoriteState(any(), any(), any(), any()) }
            assertEquals(UserNotLogged, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected, user is logged in but fails to update favorite state`() {
        val session = mockk<Session>()
        val userAccount = mockk<UserAccount>()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns userAccount
        every { moviesRepository.updateMovieFavoriteState(any(), any(), any(), any()) } returns false

        subject.favoriteMovie(movie, true).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { accountRepository.getUserAccount(session) }
            verify(exactly = 1) { moviesRepository.updateMovieFavoriteState(movie, true, userAccount, session) }
            assertEquals(ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected, user is logged and can update favorite status`() {
        val session = mockk<Session>()
        val userAccount = mockk<UserAccount>()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns userAccount
        every { moviesRepository.updateMovieFavoriteState(any(), any(), any(), any()) } returns true

        subject.favoriteMovie(movie, true).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { accountRepository.getUserAccount(session) }
            verify(exactly = 1) { moviesRepository.updateMovieFavoriteState(movie, true, userAccount, session) }
            assertEquals(Success, result)
        }
    }


    private companion object {
        val movie = mockk<Movie>()
    }
}