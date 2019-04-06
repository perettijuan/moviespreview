package com.jpp.mpdomain.usecase.account

import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.SessionRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import com.jpp.mpdomain.usecase.account.GetFavoriteMoviesUseCase.FavoriteMoviesResult.*
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(MockKExtension::class)
class GetFavoriteMoviesUseCaseTest {

    @RelaxedMockK
    private lateinit var accountRepository: AccountRepository
    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @RelaxedMockK
    private lateinit var sessionRepository: SessionRepository
    @RelaxedMockK
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: GetFavoriteMoviesUseCase


    @BeforeEach
    fun setUp() {
        subject = GetFavoriteMoviesUseCase.Impl(
                sessionRepository,
                accountRepository,
                languageRepository,
                connectivityRepository
        )
    }

    @Test
    fun `Should check connectivity before fetching movies and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.getUserFavoriteMovies(1).let { result ->
            verify(exactly = 0) { sessionRepository.getCurrentSession() }
            verify(exactly = 0) { accountRepository.getUserAccount(any()) }
            verify(exactly = 0) { accountRepository.getFavoriteMovies(any(), any(), any(), any()) }
            assertEquals(ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return UserNotLogged when connected to network but user is not logged`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns null

        subject.getUserFavoriteMovies(1).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 0) { accountRepository.getUserAccount(any()) }
            verify(exactly = 0) { accountRepository.getFavoriteMovies(any(), any(), any(), any()) }
            assertEquals(UserNotLogged, result)
        }
    }

    @Test
    fun `Should return UserNotLogged when connected to network but user is not logged - no account info`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns mockk()
        every { accountRepository.getUserAccount(any()) } returns null

        subject.getUserFavoriteMovies(1).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { accountRepository.getUserAccount(any()) }
            verify(exactly = 0) { accountRepository.getFavoriteMovies(any(), any(), any(), any()) }
            assertEquals(UserNotLogged, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns mockk()
        every { accountRepository.getUserAccount(any()) } returns mockk()
        every { accountRepository.getFavoriteMovies(any(), any(), any(), any()) } returns null

        subject.getUserFavoriteMovies(1).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { accountRepository.getUserAccount(any()) }
            verify(exactly = 1) { accountRepository.getFavoriteMovies(any(), any(), any(), any()) }
            assertEquals(ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return NoFavorites when the user has no favorites movies`() {
        val session = mockk<Session>()
        val userAccount = mockk<UserAccount>()
        val results = listOf<Movie>()
        val favPage = mockk<MoviePage>()

        every { favPage.results } returns results
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns userAccount
        every { accountRepository.getFavoriteMovies(any(), any(), any(), any()) } returns favPage
        every { languageRepository.getCurrentDeviceLanguage() } returns SupportedLanguage.English

        subject.getUserFavoriteMovies(1).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { accountRepository.getUserAccount(any()) }
            verify(exactly = 1) { accountRepository.getFavoriteMovies(1, userAccount, session, SupportedLanguage.English) }
            assertEquals(NoFavorites, result)
        }
    }

    @Test
    fun `Should return Success when the user has favorites movies`() {
        val session = mockk<Session>()
        val userAccount = mockk<UserAccount>()
        val results = listOf(mockk<Movie>(), mockk(), mockk())
        val favPage = mockk<MoviePage>()

        every { favPage.results } returns results
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns userAccount
        every { accountRepository.getFavoriteMovies(any(), any(), any(), any()) } returns favPage
        every { languageRepository.getCurrentDeviceLanguage() } returns SupportedLanguage.English

        subject.getUserFavoriteMovies(1).let { result ->
            verify(exactly = 1) { sessionRepository.getCurrentSession() }
            verify(exactly = 1) { accountRepository.getUserAccount(any()) }
            verify(exactly = 1) { accountRepository.getFavoriteMovies(1, userAccount, session, SupportedLanguage.English) }
            assertTrue(result is Success)
            assertEquals(3, (result as Success).moviesPage.results.size)
        }
    }
}