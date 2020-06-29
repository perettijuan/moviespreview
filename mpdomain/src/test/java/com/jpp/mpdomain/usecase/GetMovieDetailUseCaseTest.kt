package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MovieDetailRepository
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
class GetMovieDetailUseCaseTest {

    @MockK
    private lateinit var movieDetailRepository: MovieDetailRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    @MockK
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: GetMovieDetailUseCase

    @BeforeEach
    fun setUp() {
        subject = GetMovieDetailUseCase(
            movieDetailRepository,
            connectivityRepository,
            languageRepository
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
    fun `Should fail with unknown reason`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns mockk()
        coEvery { movieDetailRepository.getMovieDetails(any(), any()) } returns null

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should retrieve movie detail`() = runBlocking {
        val expected = MovieDetail(
            id = 1.0,
            title = "aTitle",
            overview = "anOverview",
            poster_path = "aPosterPath",
            release_date = "aReleaseDate",
            genres = listOf(),
            popularity = 11F,
            vote_average = 11F,
            vote_count = 50.0
        )
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery {
            movieDetailRepository.getMovieDetails(
                1.0,
                SupportedLanguage.English
            )
        } returns expected

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Success)
        assertEquals(expected, actual.getOrNull())
    }
}
