package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.*
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
        every { sessionRepository.getCurrentSession() } returns null

        val actual = subject.execute(1, param.type)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
    }

    @ParameterizedTest
    @MethodSource("testParams")
    fun `Should fail with user not logged when no account available`(param: TestParam) =
        runBlocking {
            every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
            every { sessionRepository.getCurrentSession() } returns mockk()
            every { accountRepository.getUserAccount(any()) } returns null

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
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns account
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        every {
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
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(session) } returns account
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        every { configurationRepository.getAppConfiguration() } returns AppConfiguration(
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
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(session) } returns account
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        every { configurationRepository.getAppConfiguration() } returns null

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
                    every {
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
                    every {
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
                    every {
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