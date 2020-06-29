package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.repository.ConfigurationRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ConfigureMovieImagesPathUseCaseTest {

    @MockK
    private lateinit var configurationRepository: ConfigurationRepository

    private lateinit var subject: ConfigureMovieImagesPathUseCase

    @BeforeEach
    fun setUp() {
        subject = ConfigureMovieImagesPathUseCase(configurationRepository)
    }

    @Test
    fun `Should fail if there is no AppConfiguration available`() = runBlocking {
        every { configurationRepository.getAppConfiguration() } returns null

        val result = subject.execute(movie = originalMovie)

        assertTrue(result is Try.Failure)
    }

    @Test
    fun `Should configure movie with the last available size`() = runBlocking {
        every { configurationRepository.getAppConfiguration() } returns AppConfiguration(
            imagesConfig
        )
        val expected = Movie(
            id = 15.toDouble(),
            poster_path = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
            backdrop_path = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
            title = "aMovie",
            original_title = "aTitle",
            original_language = "aLanguage",
            overview = "anOverview",
            release_date = "aReleaseDate",
            vote_count = 12.toDouble(),
            vote_average = 12F,
            popularity = 18F
        )

        val result = subject.execute(movie = originalMovie)

        assertEquals(expected, result.getOrNull())
    }

    private val originalMovie = Movie(
        id = 15.toDouble(),
        poster_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
        backdrop_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
        title = "aMovie",
        original_title = "aTitle",
        original_language = "aLanguage",
        overview = "anOverview",
        release_date = "aReleaseDate",
        vote_count = 12.toDouble(),
        vote_average = 12F,
        popularity = 18F
    )

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
