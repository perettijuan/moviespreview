package com.jpp.mpdomain.repository.search

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.paging.PageKeyedDataSource
import com.jpp.moviespreview.utiltest.InstantTaskExecutorExtension
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.repository.configuration.ConfigurationApi
import com.jpp.mpdomain.repository.configuration.ConfigurationDb
import com.jpp.mpdomain.utils.CurrentThreadExecutorService
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class SearchRepositoryTest {


    @RelaxedMockK
    private lateinit var searchApi: SearchApi
    @RelaxedMockK
    private lateinit var configurationApi: ConfigurationApi
    @RelaxedMockK
    private lateinit var configurationDb: ConfigurationDb
    @RelaxedMockK
    private lateinit var connectivityHandler: ConnectivityHandler
    @RelaxedMockK
    private lateinit var configurationHandler: ConfigurationHandler

    private lateinit var subject: SearchRepository

    private val searchQuery = "aQuery"
    private val targetImageSize = 70
    private val searchResultMapper: ((SearchResult) -> SearchResult) = { searchResult -> searchResult }


    @BeforeEach
    fun setUp() {
        subject = SearchRepositoryImpl(
                searchApi,
                configurationApi,
                configurationDb,
                connectivityHandler,
                configurationHandler,
                CurrentThreadExecutorService()
        )
    }

    @Test
    fun `Repository should fetch data from API`() {
        val searchPage = mockk<SearchPage>()
        val searchResults = listOf<SearchResult>(mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))

        every { searchPage.results } returns searchResults
        every { searchApi.performSearch(any(), any()) } returns searchPage

        executeSearchPageInRepository(2) {
            verify { searchApi.performSearch(searchQuery, 2) }
            assertEquals(SearchRepositoryState.Loaded, it)
        }
    }

    @Test
    fun `Repository should fetch data and filter only movies and persons`() {
        val movieSearchResult = mockk<SearchResult>(relaxed = true)
        val personSearchResult = mockk<SearchResult>(relaxed = true)
        val tvSearchResult = mockk<SearchResult>(relaxed = true)
        val searchPage = mockk<SearchPage>()
        val searchResults = listOf(movieSearchResult, personSearchResult, tvSearchResult)

        every { searchPage.results } returns searchResults
        every { movieSearchResult.isMovie() } returns true
        every { personSearchResult.isPerson() } returns true
        every { tvSearchResult.isTvShow() } returns true
        every { configurationHandler.configureSearchResult(movieSearchResult, any(), any()) } returns movieSearchResult
        every { configurationHandler.configureSearchResult(personSearchResult, any(), any()) } returns personSearchResult
        every { configurationHandler.configureSearchResult(tvSearchResult, any(), any()) } returns tvSearchResult
        every { searchApi.performSearch(any(), any()) } returns searchPage

        executeSearchPageInRepositoryAndVerifyListResult(2) {
            assertEquals(2, it.size)
            assertTrue(it.contains(movieSearchResult))
            assertTrue(it.contains(personSearchResult))
            assertFalse(it.contains(tvSearchResult))
        }
    }

    @Test
    fun `Repository should config movies path using the application configuration`() {
        val movieSearchResult = mockk<SearchResult>(relaxed = true)
        val searchPage = mockk<SearchPage>()
        val searchResults = listOf(movieSearchResult)

        val appConfig = mockk<AppConfiguration>()
        val imagesConfig = mockk<ImagesConfiguration>()

        every { searchPage.results } returns searchResults
        every { movieSearchResult.isMovie() } returns true
        every { configurationHandler.configureSearchResult(movieSearchResult, any(), any()) } returns movieSearchResult
        every { searchApi.performSearch(any(), any()) } returns searchPage
        every { appConfig.images } returns imagesConfig
        every { configurationDb.getAppConfiguration() } returns appConfig

        executeSearchPageInRepository(2) {
            verify { configurationHandler.configureSearchResult(movieSearchResult, imagesConfig, targetImageSize) }
        }
    }

    @Test
    fun `Repository should fetch config from API and store it locally when no data is in database`() {
        val movieSearchResult = mockk<SearchResult>(relaxed = true)
        val searchPage = mockk<SearchPage>()
        val searchResults = listOf(movieSearchResult)

        val appConfig = mockk<AppConfiguration>()
        val imagesConfig = mockk<ImagesConfiguration>()

        every { searchPage.results } returns searchResults
        every { movieSearchResult.isMovie() } returns true
        every { configurationHandler.configureSearchResult(movieSearchResult, any(), any()) } returns movieSearchResult
        every { searchApi.performSearch(any(), any()) } returns searchPage
        every { appConfig.images } returns imagesConfig
        every { configurationDb.getAppConfiguration() } returns null
        every { configurationApi.getAppConfiguration() } returns appConfig

        executeSearchPageInRepository(2) {
            verify { configurationDb.getAppConfiguration() }
            verify { configurationDb.saveAppConfiguration(appConfig) }
            verify { configurationApi.getAppConfiguration() }
        }
    }


    @Test
    fun `Repository should notify error unknown with items when an error occurs retrieving movie page`() {
        every { searchApi.performSearch(any(), any()) } returns null
        every { connectivityHandler.isConnectedToNetwork() } returns true
        executeSearchPageInRepository(2) {
            assertEquals(SearchRepositoryState.ErrorUnknown(true), it)
        }
    }

    @Test
    fun `Repository should notify error unknown without when an error occurs retrieving first movie page`() {
        every { searchApi.performSearch(any(), any()) } returns null
        every { connectivityHandler.isConnectedToNetwork() } returns true
        executeSearchPageInRepository(1) {
            assertEquals(SearchRepositoryState.ErrorUnknown(false), it)
        }
    }


    @Test
    fun `Repository should notify connectivity error with items when an error occurs retrieving movie page`() {
        every { searchApi.performSearch(any(), any()) } returns null
        every { connectivityHandler.isConnectedToNetwork() } returns false
        executeSearchPageInRepository(2) {
            assertEquals(SearchRepositoryState.ErrorNoConnectivity(true), it)
        }
    }

    @Test
    fun `Repository should notify connectivity error without when an error occurs retrieving first movie page`() {
        every { searchApi.performSearch(any(), any()) } returns null
        every { connectivityHandler.isConnectedToNetwork() } returns false
        executeSearchPageInRepository(1) {
            assertEquals(SearchRepositoryState.ErrorNoConnectivity(false), it)
        }
    }


    private fun executeSearchPageInRepository(page: Int,
                                              stateVerification: (SearchRepositoryState) -> Unit) {
        val lifecycleOwner: LifecycleOwner = mockk()
        val lifecycle = LifecycleRegistry(lifecycleOwner)

        every { lifecycleOwner.lifecycle } returns lifecycle

        val listing = subject.search(
                searchQuery,
                targetImageSize,
                searchResultMapper
        )

        listing.pagedList.observe(lifecycleOwner, Observer {
            val dataSource = it.dataSource as PageKeyedDataSource<Int, SearchResult>
            dataSource.loadAfter(PageKeyedDataSource.LoadParams(page, 1), mockk(relaxed = true))
        })

        listing.opState.observe(lifecycleOwner, Observer {
            stateVerification.invoke(it)
        })

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }


    private fun executeSearchPageInRepositoryAndVerifyListResult(page: Int,
                                                                 listVerification: (List<SearchResult>) -> Unit) {
        val lifecycleOwner: LifecycleOwner = mockk()
        val lifecycle = LifecycleRegistry(lifecycleOwner)

        every { lifecycleOwner.lifecycle } returns lifecycle

        val listing = subject.search(
                searchQuery,
                targetImageSize,
                searchResultMapper
        )

        listing.pagedList.observe(lifecycleOwner, Observer {
            val dataSource = it.dataSource as PageKeyedDataSource<Int, SearchResult>
            dataSource.loadAfter(PageKeyedDataSource.LoadParams(page, 1), object : PageKeyedDataSource.LoadCallback<Int, SearchResult>() {
                override fun onResult(data: MutableList<SearchResult>, adjacentPageKey: Int?) {
                    listVerification.invoke(data)
                }
            })
        })

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
}