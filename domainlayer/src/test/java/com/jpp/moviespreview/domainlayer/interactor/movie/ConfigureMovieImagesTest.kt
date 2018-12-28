package com.jpp.moviespreview.domainlayer.interactor.movie


import com.jpp.moviespreview.domainlayer.AppConfiguration
import com.jpp.moviespreview.domainlayer.ImagesConfiguration
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.interactor.ConfigureMovieImages
import com.jpp.moviespreview.domainlayer.interactor.MovieImagesParam
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class ConfigureMovieImagesTest {

    @MockK
    private lateinit var configRepository: ConfigurationRepository
    private lateinit var subject: ConfigureMovieImages

    @BeforeEach
    fun setUp() {
        subject = ConfigureMovieImagesImpl(configRepository)
    }


    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `execute should return result with new movie created`(param: ExecuteTestParameter) {
        every { configRepository.getConfiguration() } returns ConfigurationRepository.ConfigurationRepositoryOutput.Success(param.imagesConfig)

        val result = subject.execute(MovieImagesParam(param.movie, param.targetBackdropSize, param.targetPosterSize))

        assertEquals(param.expectedPosterPath, result.movie.poster_path, param.case)
        assertEquals(param.expectedBackdropPath, result.movie.backdrop_path, param.case)
    }

    @Test
    fun `execute should return result with the same movie when config in repository is failed`() {
        every { configRepository.getConfiguration() } returns ConfigurationRepository.ConfigurationRepositoryOutput.Error

        val result = subject.execute(MovieImagesParam(movie, 780, 340))

        assertEquals(movie, result.movie)
    }

    data class ExecuteTestParameter(
            val case: String,
            val imagesConfig: AppConfiguration,
            val movie: Movie,
            val targetBackdropSize: Int,
            val targetPosterSize: Int,
            val expectedPosterPath: String?,
            val expectedBackdropPath: String?
    )

    companion object {

        @JvmStatic
        fun executeParameters() = listOf(
                ExecuteTestParameter(
                        case = "Should configure with exact value",
                        imagesConfig = AppConfiguration(imagesConfig),
                        movie = movie,
                        targetBackdropSize = 780,
                        targetPosterSize = 342,
                        expectedBackdropPath = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedPosterPath = "baseUrl/w342/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteTestParameter(
                        case = "Should configure with first higher value",
                        imagesConfig = AppConfiguration(imagesConfig),
                        movie = movie,
                        targetBackdropSize = 800,
                        targetPosterSize = 551,
                        expectedBackdropPath = "baseUrl/w1280/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedPosterPath = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteTestParameter(
                        case = "Should configure with first lower value",
                        imagesConfig = AppConfiguration(imagesConfig),
                        movie = movie,
                        targetBackdropSize = 40,
                        targetPosterSize = 40,
                        expectedBackdropPath = "baseUrl/w300/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedPosterPath = "baseUrl/w92/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteTestParameter(
                        case = "Should configure with last when no value is accepted",
                        imagesConfig = AppConfiguration(imagesConfig),
                        movie = movie,
                        targetBackdropSize = 1400,
                        targetPosterSize = 800,
                        expectedBackdropPath = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedPosterPath = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteTestParameter(
                        case = "Should leave as null when paths are null",
                        imagesConfig = AppConfiguration(imagesConfig),
                        movie = movie.copy(backdrop_path = null, poster_path = null),
                        targetBackdropSize = 1400,
                        targetPosterSize = 800,
                        expectedBackdropPath = null,
                        expectedPosterPath = null
                )
        )

        private val imagesConfig = ImagesConfiguration(
                base_url = "baseUrl/",
                poster_sizes = listOf("w92",
                        "w154",
                        "w185",
                        "w342",
                        "w500",
                        "w780",
                        "original"),
                profile_sizes = listOf("w45",
                        "w185",
                        "h632",
                        "original"),
                backdrop_sizes = listOf("w300",
                        "w780",
                        "w1280",
                        "original")

        )

        private val movie = Movie(
                id = 12.toDouble(),
                title = "Titanic",
                original_title = "Titanic",
                overview = "An overview",
                release_date = "",
                original_language = "En",
                poster_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                backdrop_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                vote_count = 1233.toDouble(),
                vote_average = 0F,
                popularity = 0F
        )
    }
}