package com.jpp.mpdomain.handlers.configuration

import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.SearchResult
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class ConfigurationHandlerTest {

    private lateinit var subject: ConfigurationHandler

    @BeforeEach
    fun setUp() {
        subject = ConfigurationHandlerImpl()
    }


    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `Should return a new configured movie`(param: ExecuteTestParameter) {
        val result = subject.configureMovieImagesPath(
                param.movie,
                param.imagesConfig,
                param.targetBackdropSize,
                param.targetPosterSize
        )

        assertEquals(param.expectedPosterPath, result.poster_path, param.case)
        assertEquals(param.expectedBackdropPath, result.backdrop_path, param.case)
    }

    @ParameterizedTest
    @MethodSource("executeSearchParameters")
    fun `Should return a new configured result`(param: ExecuteSearchTestParameter) {
        val result = subject.configureSearchResult(
                param.searchResult,
                param.imagesConfig,
                param.targetImageSize
        )

        assertEquals(param.expectedBackdropPath, result.backdrop_path)
        assertEquals(param.expectedPosterPath, result.poster_path)
        assertEquals(param.expectedProfilePath, result.profile_path)
    }


    data class ExecuteTestParameter(
            val case: String,
            val imagesConfig: ImagesConfiguration,
            val movie: Movie,
            val targetBackdropSize: Int,
            val targetPosterSize: Int,
            val expectedPosterPath: String?,
            val expectedBackdropPath: String?
    )

    data class ExecuteSearchTestParameter(
            val case: String,
            val imagesConfig: ImagesConfiguration,
            val searchResult: SearchResult,
            val targetImageSize: Int,
            val expectedPosterPath: String?,
            val expectedBackdropPath: String?,
            val expectedProfilePath: String?
    )

    companion object {

        @JvmStatic
        fun executeParameters() = listOf(
                ExecuteTestParameter(
                        case = "Should configure with exact value",
                        imagesConfig = imagesConfig,
                        movie = movie,
                        targetBackdropSize = 780,
                        targetPosterSize = 342,
                        expectedBackdropPath = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedPosterPath = "baseUrl/w342/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteTestParameter(
                        case = "Should configure with first higher value",
                        imagesConfig = imagesConfig,
                        movie = movie,
                        targetBackdropSize = 800,
                        targetPosterSize = 551,
                        expectedBackdropPath = "baseUrl/w1280/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedPosterPath = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteTestParameter(
                        case = "Should configure with first lower value",
                        imagesConfig = imagesConfig,
                        movie = movie,
                        targetBackdropSize = 40,
                        targetPosterSize = 40,
                        expectedBackdropPath = "baseUrl/w300/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedPosterPath = "baseUrl/w92/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteTestParameter(
                        case = "Should configure with last when no value is accepted",
                        imagesConfig = imagesConfig,
                        movie = movie,
                        targetBackdropSize = 1400,
                        targetPosterSize = 800,
                        expectedBackdropPath = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedPosterPath = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteTestParameter(
                        case = "Should leave as null when paths are null",
                        imagesConfig = imagesConfig,
                        movie = movie.copy(backdrop_path = null, poster_path = null),
                        targetBackdropSize = 1400,
                        targetPosterSize = 800,
                        expectedBackdropPath = null,
                        expectedPosterPath = null
                )
        )


        @JvmStatic
        fun executeSearchParameters() = listOf(
                ExecuteSearchTestParameter(
                        case = "Should configure movie with exact value",
                        imagesConfig = imagesConfig,
                        searchResult = searchResultMovie,
                        targetImageSize = 780,
                        expectedBackdropPath = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedPosterPath = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedProfilePath = null
                ),
                ExecuteSearchTestParameter(
                        case = "Should configure person with exact value",
                        imagesConfig = imagesConfig,
                        searchResult = searchResultPerson,
                        targetImageSize = 45,
                        expectedBackdropPath = null,
                        expectedPosterPath = null,
                        expectedProfilePath = "baseUrl/w45/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteSearchTestParameter(
                        case = "Should configure movie with first higher value",
                        imagesConfig = imagesConfig,
                        searchResult = searchResultMovie,
                        targetImageSize = 350,
                        expectedBackdropPath = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedPosterPath = "baseUrl/w500/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                        expectedProfilePath = null
                ),
                ExecuteSearchTestParameter(
                        case = "Should configure person with first higher value",
                        imagesConfig = imagesConfig,
                        searchResult = searchResultPerson,
                        targetImageSize = 190,
                        expectedBackdropPath = null,
                        expectedPosterPath = null,
                        expectedProfilePath = "baseUrl/h632/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
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

        private val searchResultMovie = SearchResult(
                id = 15.toDouble(),
                poster_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                backdrop_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                profile_path = null,
                media_type = "movie",
                title = "aMovie",
                name = null,
                original_title = null,
                original_language = null,
                overview = null,
                release_date = null,
                genre_ids = null,
                vote_count = null,
                vote_average = null,
                popularity = null
        )

        private val searchResultPerson = SearchResult(
                id = 15.toDouble(),
                poster_path = null,
                backdrop_path = null,
                profile_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                media_type = "person",
                title = null,
                name = "aPerson",
                original_title = null,
                original_language = null,
                overview = null,
                release_date = null,
                genre_ids = null,
                vote_count = null,
                vote_average = null,
                popularity = null
        )
    }
}