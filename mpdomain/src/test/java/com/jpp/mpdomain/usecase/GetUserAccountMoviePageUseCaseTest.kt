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
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class GetUserAccountMoviePageUseCaseTest {

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

    private lateinit var subject: GetUserAccountMoviePageUseCase

    @BeforeEach
    fun setUp() {
        subject = GetUserAccountMoviePageUseCase(
            moviePageRepository,
            sessionRepository,
            accountRepository,
            configurationRepository,
            languageRepository,
            connectivityRepository
        )
    }

    @ParameterizedTest
    @MethodSource("testParams")
    fun `Should fail with no connectivity message`(param: TestParam) = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute(1, param.type)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @ParameterizedTest
    @MethodSource("testParams")
    fun `Should fail with user not logged`(param: TestParam) = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns null

        val actual = subject.execute(1, param.type)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
    }

    @ParameterizedTest
    @MethodSource("testParams")
    fun `Should fail with user not logged when no account available`(param: TestParam) =
        runBlocking {
            every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
            coEvery { sessionRepository.getCurrentSession() } returns mockk()
            coEvery { accountRepository.getUserAccount(any()) } returns null

            val actual = subject.execute(1, param.type)

            assertTrue(actual is Try.Failure)
            assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
        }

    @ParameterizedTest
    @MethodSource("testParams")
    fun `Should fail with unknown reason`(param: TestParam) = runBlocking {
        val session = mockk<Session>()
        val account = mockk<UserAccount>()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns session
        coEvery { accountRepository.getUserAccount(any()) } returns account
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery {
            moviePageRepository.getFavoriteMoviePage(any(), any(), any(), any())
        } returns null

        param.preCondition(
            moviePageRepository,
            1,
            session,
            account,
            SupportedLanguage.English,
            null
        )

        val actual = subject.execute(1, param.type)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @ParameterizedTest
    @MethodSource("testParams")
    fun `Should retrieve and configure movies`(param: TestParam) = runBlocking {
        val session = mockk<Session>()
        val account = mockk<UserAccount>()
        val moviePage = MoviePage(
            page = 1,
            results = mockedMovieList,
            total_pages = 10,
            total_results = 500
        )
        val expected = moviePage.copy(
            results = moviePage.results.getImagesConfiguredMovies()
        )

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns session
        coEvery { accountRepository.getUserAccount(session) } returns account
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { configurationRepository.getAppConfiguration() } returns AppConfiguration(
            imagesConfig
        )
        param.preCondition(
            moviePageRepository,
            1,
            session,
            account,
            SupportedLanguage.English,
            moviePage
        )

        val actual = subject.execute(1, param.type)

        assertTrue(actual is Try.Success)
        assertEquals(expected, actual.getOrNull())
    }

    @ParameterizedTest
    @MethodSource("testParams")
    fun `Should retrieve when no app configuration available`(param: TestParam) = runBlocking {
        val session = mockk<Session>()
        val account = mockk<UserAccount>()
        val moviePage = MoviePage(
            page = 1,
            results = mockedMovieList,
            total_pages = 10,
            total_results = 500
        )

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { sessionRepository.getCurrentSession() } returns session
        coEvery { accountRepository.getUserAccount(session) } returns account
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { configurationRepository.getAppConfiguration() } returns null

        param.preCondition(
            moviePageRepository,
            1,
            session,
            account,
            SupportedLanguage.English,
            moviePage
        )

        val actual = subject.execute(1, param.type)

        assertTrue(actual is Try.Success)
        assertEquals(moviePage, actual.getOrNull())
    }

    data class TestParam(
        val type: AccountMovieType,
        val preCondition: (MoviePageRepository, Int, Session, UserAccount, SupportedLanguage, MoviePage?) -> Unit
    )

    companion object {
        @JvmStatic
        fun testParams() = listOf(
            TestParam(
                type = AccountMovieType.Favorite,
                preCondition = { repository, page, session, userAccount, language, moviePage ->
                    coEvery {
                        repository.getFavoriteMoviePage(
                            page,
                            userAccount,
                            session,
                            language
                        )
                    } returns moviePage
                }
            ),
            TestParam(
                type = AccountMovieType.Watchlist,
                preCondition = { repository, page, session, userAccount, language, moviePage ->
                    coEvery {
                        repository.getWatchlistMoviePage(
                            page,
                            userAccount,
                            session,
                            language
                        )
                    } returns moviePage
                }
            ),
            TestParam(
                type = AccountMovieType.Rated,
                preCondition = { repository, page, session, userAccount, language, moviePage ->
                    coEvery {
                        repository.getRatedMoviePage(
                            page,
                            userAccount,
                            session,
                            language
                        )
                    } returns moviePage
                }
            )
        )
    }
}
