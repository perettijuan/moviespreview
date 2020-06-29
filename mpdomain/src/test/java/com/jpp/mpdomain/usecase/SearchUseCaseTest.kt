package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.SearchRepository
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

@ExtendWith(MockKExtension::class)
class SearchUseCaseTest {

    @MockK
    private lateinit var searchRepository: SearchRepository

    @MockK
    private lateinit var configurationRepository: ConfigurationRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    @MockK
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: SearchUseCase

    @BeforeEach
    fun setUp() {
        subject = SearchUseCase(
            searchRepository,
            configurationRepository,
            connectivityRepository,
            languageRepository
        )
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute("aQuery", 1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { searchRepository.searchPage(any(), any(), any()) } returns null

        val actual = subject.execute("aQuery", 1)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should perform search and configure search results`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { configurationRepository.getAppConfiguration() } returns AppConfiguration(
            IMAGES_CONFIG
        )
        coEvery {
            searchRepository.searchPage(
                "aQuery",
                1,
                SupportedLanguage.English
            )
        } returns SearchPage(
            page = 1,
            results = SEARCH_RESULTS,
            total_pages = 10,
            total_results = 150
        )

        val result = subject.execute("aQuery", 1)

        assertTrue(result is Try.Success)
        assertEquals(1, result.getOrNull()?.page)
        assertEquals(10, result.getOrNull()?.total_pages)
        assertEquals(150, result.getOrNull()?.total_results)
        assertEquals(4, result.getOrNull()?.results?.size)

        // result[0]
        assertEquals(12.0, result.getOrNull()?.results?.get(0)?.id)
        assertEquals("aPerson12", result.getOrNull()?.results?.get(0)?.name)
        assertEquals("baseUrl/original/12.jpg", result.getOrNull()?.results?.get(0)?.profile_path)

        // result[1]
        assertEquals(14.0, result.getOrNull()?.results?.get(1)?.id)
        assertEquals("aMovie14", result.getOrNull()?.results?.get(1)?.title)
        assertEquals("baseUrl/original/14P.jpg", result.getOrNull()?.results?.get(1)?.poster_path)
        assertEquals("baseUrl/original/14B.jpg", result.getOrNull()?.results?.get(1)?.backdrop_path)

        // result[1]
        assertEquals(15.0, result.getOrNull()?.results?.get(2)?.id)
        assertEquals("aMovie15", result.getOrNull()?.results?.get(2)?.title)
        assertEquals("baseUrl/original/15P.jpg", result.getOrNull()?.results?.get(2)?.poster_path)
        assertEquals("baseUrl/original/15B.jpg", result.getOrNull()?.results?.get(2)?.backdrop_path)

        // result[3]
        assertEquals(13.0, result.getOrNull()?.results?.get(3)?.id)
        assertEquals("aPerson13", result.getOrNull()?.results?.get(3)?.name)
        assertEquals("baseUrl/original/13.jpg", result.getOrNull()?.results?.get(3)?.profile_path)
    }

    @Test
    fun `Should perform search but not configure search results`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { configurationRepository.getAppConfiguration() } returns null

        coEvery {
            searchRepository.searchPage(
                "aQuery",
                1,
                SupportedLanguage.English
            )
        } returns SearchPage(
            page = 1,
            results = SEARCH_RESULTS,
            total_pages = 10,
            total_results = 150
        )

        val result = subject.execute("aQuery", 1)

        assertTrue(result is Try.Success)
        assertEquals(1, result.getOrNull()?.page)
        assertEquals(10, result.getOrNull()?.total_pages)
        assertEquals(150, result.getOrNull()?.total_results)
        assertEquals(4, result.getOrNull()?.results?.size)
        assertEquals(SEARCH_RESULTS, result.getOrNull()?.results)
    }

    private companion object {
        val SEARCH_RESULTS = listOf(
            SearchResult(
                id = 12.0,
                poster_path = null,
                backdrop_path = null,
                profile_path = "/12.jpg",
                media_type = "person",
                title = null,
                name = "aPerson12",
                original_title = null,
                original_language = null,
                overview = null,
                release_date = null,
                genre_ids = null,
                vote_count = null,
                vote_average = null,
                popularity = null
            ),
            SearchResult(
                id = 14.0,
                poster_path = "/14P.jpg",
                backdrop_path = "/14B.jpg",
                profile_path = null,
                media_type = "movie",
                title = "aMovie14",
                name = null,
                original_title = null,
                original_language = null,
                overview = null,
                release_date = null,
                genre_ids = null,
                vote_count = null,
                vote_average = null,
                popularity = null
            ),
            SearchResult(
                id = 15.0,
                poster_path = "/15P.jpg",
                backdrop_path = "/15B.jpg",
                profile_path = null,
                media_type = "movie",
                title = "aMovie15",
                name = null,
                original_title = null,
                original_language = null,
                overview = null,
                release_date = null,
                genre_ids = null,
                vote_count = null,
                vote_average = null,
                popularity = null
            ),
            SearchResult(
                id = 13.0,
                poster_path = null,
                backdrop_path = null,
                profile_path = "/13.jpg",
                media_type = "person",
                title = null,
                name = "aPerson13",
                original_title = null,
                original_language = null,
                overview = null,
                release_date = null,
                genre_ids = null,
                vote_count = null,
                vote_average = null,
                popularity = null
            )
        )

        private val IMAGES_CONFIG = ImagesConfiguration(
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
}
