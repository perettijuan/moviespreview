package com.jpp.mpdomain.usecase.movies

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.repository.configuration.ConfigurationRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ConfigMovieUseCaseTest {

    @RelaxedMockK
    private lateinit var configurationRepository: ConfigurationRepository
    @RelaxedMockK
    private lateinit var configurationHandler: ConfigurationHandler

    private lateinit var subject: ConfigMovieUseCase

    @BeforeEach
    fun setUp() {
        subject = ConfigMovieUseCase.Impl(configurationRepository, configurationHandler)
    }

    @Test
    fun `Should return same search movie when unable to fetch app config`() {
        val expectedResult = mockk<Movie>()

        every { configurationRepository.getAppConfiguration() } returns null

        val actualResult = subject.configure(10, expectedResult)

        verify(exactly = 0) { configurationHandler.configureMovieImagesPath(any(), any(), any(), any()) }
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `Should configure movie with provided app config`() {
        val expectedResult = mockk<Movie>()
        val appConfig = mockk<AppConfiguration>()
        val imagesConfig = mockk<ImagesConfiguration>()

        every { configurationRepository.getAppConfiguration() } returns appConfig
        every { configurationHandler.configureMovieImagesPath(any(), any(), any(), any()) } returns expectedResult
        every { appConfig.images } returns imagesConfig

        val actualResult = subject.configure(10, expectedResult)

        verify { configurationHandler.configureMovieImagesPath(expectedResult, imagesConfig, 10, 10) }
        Assertions.assertEquals(expectedResult, actualResult)
    }
}