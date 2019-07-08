package com.jpp.mpdomain.interactors

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.SearchResult
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
    fun `Should configure movie images path`(param: MoviePathConfigParam) {
        val configured = subject.configurePathMovie(param.targetImageSize, param.targetImageSize, param.movie)

        assertEquals(param.expected, configured)
    }

    @ParameterizedTest
    @MethodSource("executeSearchParameters")
    fun `Should configure search result images path`(param: SearchResultPathParam) {
        val configured = subject.configureSearchResult(param.targetImageSize, param.searchResult)

        assertEquals(param.expected, configured)
    }


    data class MoviePathConfigParam(
            val targetImageSize: Int,
            val movie: Movie,
            val expected: Movie
    )

    data class SearchResultPathParam(
            val targetImageSize: Int,
            val searchResult: SearchResult,
            val expected: SearchResult
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
                MoviePathConfigParam(
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
                MoviePathConfigParam(
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
                MoviePathConfigParam(
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
                MoviePathConfigParam(
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

        @JvmStatic
        fun executeSearchParameters() = listOf(
                SearchResultPathParam(
                        targetImageSize = 780,
                        searchResult = searchResultMovie,
                        expected = searchResultMovie.copy(
                                backdrop_path = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                                poster_path ="baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                        )
                ),
                SearchResultPathParam(
                        targetImageSize = 45,
                        searchResult = searchResultPerson,
                        expected = searchResultPerson.copy(
                                profile_path = "baseUrl/w45/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                        )
                ),
                SearchResultPathParam(
                        targetImageSize = 350,
                        searchResult = searchResultMovie,
                        expected = searchResultMovie.copy(
                                backdrop_path = "baseUrl/w780/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                                poster_path = "baseUrl/w500/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                        )
                ),
                SearchResultPathParam(
                        targetImageSize = 190,
                        searchResult = searchResultPerson,
                        expected = searchResultPerson.copy(
                                profile_path = "baseUrl/h632/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
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