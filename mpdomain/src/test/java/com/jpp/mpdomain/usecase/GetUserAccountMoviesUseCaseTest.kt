package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.AccountMovieType
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository
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
class GetUserAccountMoviesUseCaseTest {

    @MockK
    private lateinit var moviePageRepository: MoviePageRepository

    @MockK
    private lateinit var sessionRepository: SessionRepository

    @MockK
    private lateinit var accountRepository: AccountRepository

    @MockK
    private lateinit var configurationRepository: ConfigurationRepository

    @MockK
    private lateinit var languageRepository: LanguageRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: GetUserAccountMoviesUseCase

    @BeforeEach
    fun setUp() {
        subject = GetUserAccountMoviesUseCase(
            moviePageRepository,
            sessionRepository,
            accountRepository,
            configurationRepository,
            languageRepository,
            connectivityRepository
        )
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute(1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with user not logged`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns null

        val actual = subject.execute(1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with user not logged when no account available`() =
        runBlocking {
            every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
            coEvery { sessionRepository.getCurrentSession() } returns mockk()
            coEvery { accountRepository.getUserAccount(any()) } returns null

            val actual = subject.execute(1)

            assertTrue(actual is Try.Failure)
            assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
        }

    @Test
    fun `Should fail with unknown reason when no favorites`() = runBlocking {
        val session = mockk<Session>()
        val account = mockk<UserAccount>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns session
        coEvery { accountRepository.getUserAccount(any()) } returns account
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { configurationRepository.getAppConfiguration() } returns AppConfiguration(imagesConfig)
        coEvery {
            moviePageRepository.getWatchlistMoviePage(any(), any(), any(), any())
        } returns MoviePage(1, mockedMovieList, 10, 100)
        coEvery {
            moviePageRepository.getRatedMoviePage(any(), any(), any(), any())
        } returns MoviePage(1, mockedMovieList, 10, 100)
        coEvery {
            moviePageRepository.getFavoriteMoviePage(any(), any(), any(), any())
        } returns null

        val actual = subject.execute(1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason when no watchlist`() = runBlocking {
        val session = mockk<Session>()
        val account = mockk<UserAccount>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns session
        coEvery { accountRepository.getUserAccount(any()) } returns account
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { configurationRepository.getAppConfiguration() } returns AppConfiguration(imagesConfig)
        coEvery {
            moviePageRepository.getFavoriteMoviePage(any(), any(), any(), any())
        } returns MoviePage(1, mockedMovieList, 10, 100)
        coEvery {
            moviePageRepository.getRatedMoviePage(any(), any(), any(), any())
        } returns MoviePage(1, mockedMovieList, 10, 100)
        coEvery {
            moviePageRepository.getWatchlistMoviePage(any(), any(), any(), any())
        } returns null

        val actual = subject.execute(1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason when no rated`() = runBlocking {
        val session = mockk<Session>()
        val account = mockk<UserAccount>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns session
        coEvery { accountRepository.getUserAccount(any()) } returns account
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { configurationRepository.getAppConfiguration() } returns AppConfiguration(imagesConfig)
        coEvery {
            moviePageRepository.getFavoriteMoviePage(any(), any(), any(), any())
        } returns MoviePage(1, mockedMovieList, 10, 100)
        coEvery {
            moviePageRepository.getWatchlistMoviePage(any(), any(), any(), any())
        } returns MoviePage(1, mockedMovieList, 10, 100)
        coEvery {
            moviePageRepository.getRatedMoviePage(any(), any(), any(), any())
        } returns null

        val actual = subject.execute(1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should retrieve and configure movies`() = runBlocking {
        val session = mockk<Session>()
        val account = mockk<UserAccount>()

        val moviePageFav = MoviePage(
            page = 1,
            results = mockedMovieList,
            total_pages = 10,
            total_results = 500
        )
        val expectedFav = moviePageFav.copy(
            results = moviePageFav.results.getImagesConfiguredMovies()
        )

        val moviePageRated = MoviePage(
            page = 1,
            results = mockedMovieList,
            total_pages = 9,
            total_results = 499
        )
        val expectedRated = moviePageRated.copy(
            results = moviePageRated.results.getImagesConfiguredMovies()
        )

        val moviePageWatch = MoviePage(
            page = 1,
            results = mockedMovieList,
            total_pages = 8,
            total_results = 488
        )
        val expectedWatch = moviePageWatch.copy(
            results = moviePageWatch.results.getImagesConfiguredMovies()
        )

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns session
        coEvery { accountRepository.getUserAccount(any()) } returns account
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { configurationRepository.getAppConfiguration() } returns AppConfiguration(imagesConfig)
        coEvery {
            moviePageRepository.getFavoriteMoviePage(1, account, session, SupportedLanguage.English)
        } returns moviePageFav
        coEvery {
            moviePageRepository.getWatchlistMoviePage(1, account, session, SupportedLanguage.English)
        } returns moviePageWatch
        coEvery {
            moviePageRepository.getRatedMoviePage(1, account, session, SupportedLanguage.English)
        } returns moviePageRated

        val actual = subject.execute(1)

        assertTrue(actual is Try.Success)
        assertEquals(expectedFav, actual.getOrNull()?.get(AccountMovieType.Favorite))
        assertEquals(expectedRated, actual.getOrNull()?.get(AccountMovieType.Rated))
        assertEquals(expectedWatch, actual.getOrNull()?.get(AccountMovieType.Watchlist))
    }
}
