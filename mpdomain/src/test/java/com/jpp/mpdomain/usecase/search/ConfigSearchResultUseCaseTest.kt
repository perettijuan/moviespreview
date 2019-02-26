package com.jpp.mpdomain.usecase.search

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.repository.ConfigurationRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class ConfigSearchResultUseCaseTest {

    @RelaxedMockK
    private lateinit var configurationRepository: ConfigurationRepository

    private lateinit var subject: ConfigSearchResultUseCase

    @BeforeEach
    fun setUp() {
        subject = ConfigSearchResultUseCase.Impl(configurationRepository)
    }

    @Test
    fun `Should return same search result when unable to fetch app config`() {
        val expectedResult = mockk<SearchResult>()

        every { configurationRepository.getAppConfiguration() } returns null

        val actualResult = subject.configure(10, expectedResult)

        assertEquals(expectedResult, actualResult)
    }


    @ParameterizedTest
    @MethodSource("executeSearchParameters")
    fun `Should configure search result with provided app config`(param: ExecuteSearchTestParameter) {
        val appConfig = mockk<AppConfiguration>()

        every { configurationRepository.getAppConfiguration() } returns appConfig
        every { appConfig.images } returns param.imagesConfig

        val result = subject.configure(param.targetImageSize, param.searchResult)

        assertEquals(param.expectedBackdropPath, result.backdrop_path)
        assertEquals(param.expectedPosterPath, result.poster_path)
        assertEquals(param.expectedProfilePath, result.profile_path)
    }

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