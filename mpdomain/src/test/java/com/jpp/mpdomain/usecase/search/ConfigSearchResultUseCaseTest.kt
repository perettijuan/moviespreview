package com.jpp.mpdomain.usecase.search

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.repository.ConfigurationRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ConfigSearchResultUseCaseTest {

    @RelaxedMockK
    private lateinit var configurationRepository: ConfigurationRepository
    @RelaxedMockK
    private lateinit var configurationHandler: ConfigurationHandler

    private lateinit var subject: ConfigSearchResultUseCase

    @BeforeEach
    fun setUp() {
        subject = ConfigSearchResultUseCase.Impl(configurationRepository, configurationHandler)
    }

    @Test
    fun `Should return same search result when unable to fetch app config`() {
        val expectedResult = mockk<SearchResult>()

        every { configurationRepository.getAppConfiguration() } returns null

        val actualResult = subject.configure(10, expectedResult)

        verify(exactly = 0) { configurationHandler.configureSearchResult(any(), any(), any()) }
        assertEquals(expectedResult, actualResult)
    }


    @Test
    fun `Should configure search result with provided app config`() {
        val expectedResult = mockk<SearchResult>()
        val appConfig = mockk<AppConfiguration>()
        val imagesConfig = mockk<ImagesConfiguration>()

        every { configurationRepository.getAppConfiguration() } returns appConfig
        every { configurationHandler.configureSearchResult(any(), any(), any()) } returns expectedResult
        every { appConfig.images } returns imagesConfig

        val actualResult = subject.configure(10, expectedResult)

        verify { configurationHandler.configureSearchResult(expectedResult, imagesConfig, 10) }
        assertEquals(expectedResult, actualResult)
    }
}