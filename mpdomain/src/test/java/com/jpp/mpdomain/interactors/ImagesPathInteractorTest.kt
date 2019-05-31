package com.jpp.mpdomain.interactors

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.repository.ConfigurationRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class ImagesPathInteractorTest {

    @MockK
    private lateinit var configurationRepository: ConfigurationRepository

    private lateinit var subject: ImagesPathInteractor


    @BeforeEach
    fun setUp() {
        every { configurationRepository.getAppConfiguration() } returns AppConfiguration(imagesConfig)
        subject = ImagesPathInteractor.Impl(configurationRepository)
    }


    @ParameterizedTest
    @MethodSource("executeMovieConfigParams")
    fun `Should configure movie images path`(param: ExecutePathConfigParam) {
        val configured = subject.configurePathMovie(param.targetImageSize, param.targetImageSize, param.movie)

        assertEquals(param.expected, configured)
    }

    data class ExecutePathConfigParam(
            val targetImageSize: Int,
            val movie: Movie,
            val expected: Movie
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

    companion object {

        @JvmStatic
        fun executeMovieConfigParams() = listOf(
                ExecutePathConfigParam(
                        targetImageSize = 10,
                        movie = originalMovie,
                        expected = Movie(
                                id = 15.toDouble(),
                                poster_path = "baseUrl/w92/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                                backdrop_path = "baseUrl/w300/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                                title = "aMovie",
                                original_title = "aTitle",
                                original_language = "aLanguage",
                                overview = "anOverview",
                                release_date = "aReleaseDate",
                                vote_count = 12.toDouble(),
                                vote_average = 12F,
                                popularity = 18F
                        )
                ),
                ExecutePathConfigParam(
                        targetImageSize = 780,
                        movie = originalMovie,
                        expected = Movie(
                                id = 15.toDouble(),
                                poster_path = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                                backdrop_path = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                                title = "aMovie",
                                original_title = "aTitle",
                                original_language = "aLanguage",
                                overview = "anOverview",
                                release_date = "aReleaseDate",
                                vote_count = 12.toDouble(),
                                vote_average = 12F,
                                popularity = 18F
                        )
                ),
                ExecutePathConfigParam(
                        targetImageSize = 350,
                        movie = originalMovie,
                        expected = Movie(
                                id = 15.toDouble(),
                                poster_path = "baseUrl/w500/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                                backdrop_path = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                                title = "aMovie",
                                original_title = "aTitle",
                                original_language = "aLanguage",
                                overview = "anOverview",
                                release_date = "aReleaseDate",
                                vote_count = 12.toDouble(),
                                vote_average = 12F,
                                popularity = 18F
                        )
                ),
                ExecutePathConfigParam(
                        targetImageSize = 1500,
                        movie = originalMovie,
                        expected = Movie(
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
                )
        )

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
    }

}