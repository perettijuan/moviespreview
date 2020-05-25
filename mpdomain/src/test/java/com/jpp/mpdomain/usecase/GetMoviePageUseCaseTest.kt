package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
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
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: GetMoviePageUseCase

    @BeforeEach
    fun setUp() {
        subject = GetMoviePageUseCase(
                moviePageRepository,
                connectivityRepository,
                languageRepository
        )
    }

    @Test
    fun `Should fail with no connectivity message`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute(1, MovieSection.Playing)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @ParameterizedTest
    @MethodSource("movieSections")
    fun `Should retrieve return unknown error`(movieSection: MovieSection) {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        every {
            moviePageRepository.getMoviePageForSection(1, movieSection, SupportedLanguage.English)
        } returns null

        val result = subject.execute(1, movieSection)

        assertTrue(result is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (result as Try.Failure).cause)
    }

    @ParameterizedTest
    @MethodSource("movieSections")
    fun `Should retrieve proper movie page`(movieSection: MovieSection) {
        val expected: MoviePage = mockk()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        every {
            moviePageRepository.getMoviePageForSection(1, movieSection, SupportedLanguage.English)
        } returns expected

        val result = subject.execute(1, movieSection)

        assertTrue(result is Try.Success)
        assertEquals(expected, result.getOrNull())
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