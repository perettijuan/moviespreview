package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class GetMoviePageUseCaseTest {

    @MockK
    private lateinit var moviePageRepository: MoviePageRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    @MockK
    private lateinit var configurationRepository: ConfigurationRepository

    @MockK
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: GetMoviePageUseCase

    @BeforeEach
    fun setUp() {
        subject = GetMoviePageUseCase(
            moviePageRepository,
            configurationRepository,
            connectivityRepository,
            languageRepository
        )
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute(1, MovieSection.Playing)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @ParameterizedTest
    @MethodSource("movieSections")
    fun `Should retrieve return unknown error`(movieSection: MovieSection) = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        coEvery {
            moviePageRepository.getMoviePageForSection(1, movieSection, SupportedLanguage.English)
        } returns null

        val result = subject.execute(1, movieSection)

        assertTrue(result is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (result as Try.Failure).cause)
    }

    @ParameterizedTest
    @MethodSource("movieSections")
    fun `Should retrieve proper movie page`(movieSection: MovieSection) = runBlocking {
        val moviePage = MoviePage(
            page = 1,
            results = mockedMovieList,
            total_pages = 10,
            total_results = 500
        )

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { configurationRepository.getAppConfiguration() } returns null

        coEvery {
            moviePageRepository.getMoviePageForSection(1, movieSection, SupportedLanguage.English)
        } returns moviePage

        val result = subject.execute(1, movieSection)

        assertTrue(result is Try.Success)
        assertEquals(moviePage, result.getOrNull())
    }

    companion object {
        @JvmStatic
        fun movieSections() = listOf(
            MovieSection.Playing,
            MovieSection.Popular,
            MovieSection.TopRated,
            MovieSection.Upcoming
        )
    }
}
