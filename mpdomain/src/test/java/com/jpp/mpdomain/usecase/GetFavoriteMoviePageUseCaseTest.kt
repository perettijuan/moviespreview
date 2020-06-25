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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetFavoriteMoviePageUseCaseTest {

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

    private lateinit var subject: GetFavoriteMoviePageUseCase

    @BeforeEach
    fun setUp() {
        subject = GetFavoriteMoviePageUseCase(
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
        every { sessionRepository.getCurrentSession() } returns null

        val actual = subject.execute(1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with user not logged when no account available`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns mockk()
        every { accountRepository.getUserAccount(any()) } returns null

        val actual = subject.execute(1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.UserNotLogged, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns mockk()
        every { accountRepository.getUserAccount(any()) } returns mockk()
        every { languageRepository.getCurrentAppLanguage() } returns mockk()
        every {
            moviePageRepository.getFavoriteMoviePage(any(), any(), any(), any())
        } returns null

        val actual = subject.execute(1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should retrieve and configure movies`() = runBlocking {
        val session = mockk<Session>()
        val account = mockk<UserAccount>()
        val moviePage = MoviePage(
            page = 1,
            results = getMockedMovies(),
            total_pages = 10,
            total_results = 500
        )
        val expected = moviePage.copy(
            results = getExpectedMovies(moviePage.results)
        )

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(session) } returns account
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        every { configurationRepository.getAppConfiguration() } returns AppConfiguration(imagesConfig)
        every {
            moviePageRepository.getFavoriteMoviePage(
                page = 1,
                session = session,
                userAccount = account,
                language = SupportedLanguage.English
            )
        } returns moviePage

        val actual = subject.execute(1)

        assertTrue(actual is Try.Success)
        assertEquals(expected, actual.getOrNull())
    }

    @Test
    fun `Should retrieve when no app configuration available`() = runBlocking {
        val session = mockk<Session>()
        val account = mockk<UserAccount>()
        val moviePage = MoviePage(
            page = 1,
            results = getMockedMovies(),
            total_pages = 10,
            total_results = 500
        )

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(session) } returns account
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        every { configurationRepository.getAppConfiguration() } returns null
        every {
            moviePageRepository.getFavoriteMoviePage(
                page = 1,
                session = session,
                userAccount = account,
                language = SupportedLanguage.English
            )
        } returns moviePage

        val actual = subject.execute(1)

        assertTrue(actual is Try.Success)
        assertEquals(moviePage, actual.getOrNull())
    }

    private fun getMockedMovies(): List<Movie> {
        return mutableListOf<Movie>().apply {
            for (i in 0..50) {
                add(
                    Movie(
                        id = i.toDouble(),
                        title = "titleRes$i",
                        original_language = "oTitle$i",
                        overview = "overview$i",
                        release_date = "releaseDate$i",
                        original_title = "originalLanguage$i",
                        poster_path = "posterPath$i",
                        backdrop_path = "backdropPath$i",
                        vote_count = i.toDouble(),
                        vote_average = i.toFloat(),
                        popularity = i.toFloat()
                    )
                )
            }
        }
    }

    private fun getExpectedMovies(original: List<Movie>): List<Movie> {
        return original.toMutableList().mapIndexed { index, movie ->
            movie.copy(
                poster_path = "baseUrl/originalposterPath$index",
                backdrop_path = "baseUrl/originalbackdropPath$index"
            )
        }
    }

    private val imagesConfig = ImagesConfiguration(
        base_url = "baseUrl/",
        poster_sizes = listOf(
            "w92",
            "w154",
            "w185",
            "w342",
            "w500",
            "w780",
            "original"
        ),
        profile_sizes = listOf(
            "w45",
            "w185",
            "h632",
            "original"
        ),
        backdrop_sizes = listOf(
            "w300",
            "w780",
            "w1280",
            "original"
        )

    )
}