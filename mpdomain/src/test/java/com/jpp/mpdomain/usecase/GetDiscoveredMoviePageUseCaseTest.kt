package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetDiscoveredMoviePageUseCaseTest {

    @MockK
    private lateinit var moviePageRepository: MoviePageRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    @MockK
    private lateinit var configurationRepository: ConfigurationRepository

    @MockK
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: GetDiscoveredMoviePageUseCase

    @BeforeEach
    fun setUp() {
        subject = GetDiscoveredMoviePageUseCase(
            moviePageRepository,
            configurationRepository,
            connectivityRepository,
            languageRepository
        )
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        coEvery { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute(1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown error`() = runBlocking {
        coEvery { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        coEvery {
            moviePageRepository.discover(1, SupportedLanguage.English)
        } returns null

        val result = subject.execute(1)

        assertTrue(result is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (result as Try.Failure).cause)
    }

    @Test
    fun `Should retrieve proper movie page`() = runBlocking {
        val moviePage = MoviePage(
            page = 1,
            results = mockedMovieList,
            total_pages = 10,
            total_results = 500
        )

        coEvery { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { configurationRepository.getAppConfiguration() } returns null

        coEvery {
            moviePageRepository.discover(1, SupportedLanguage.English)
        } returns moviePage

        val result = subject.execute(1)

        assertTrue(result is Try.Success)
        assertEquals(moviePage, result.getOrNull())
    }
}