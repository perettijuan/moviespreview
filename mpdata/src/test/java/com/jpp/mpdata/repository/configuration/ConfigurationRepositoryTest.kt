package com.jpp.mpdata.repository.configuration

import com.jpp.mpdata.datasources.configuration.ConfigurationApi
import com.jpp.mpdata.datasources.configuration.ConfigurationDb
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.repository.ConfigurationRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ConfigurationRepositoryTest {

    @RelaxedMockK
    private lateinit var configurationApi: ConfigurationApi
    @RelaxedMockK
    private lateinit var configurationDb: ConfigurationDb

    private lateinit var subject: ConfigurationRepository

    @BeforeEach
    fun setUp() {
        subject = ConfigurationRepositoryImpl(configurationApi, configurationDb)
    }

    @Test
    fun `Should never get from API when data is in cache`() = runBlocking {
        every { configurationDb.getAppConfiguration() } returns mockk()

        subject.getAppConfiguration()

        verify { configurationDb.getAppConfiguration() }
        verify(exactly = 0) { configurationApi.getAppConfiguration() }
        verify(exactly = 0) { configurationDb.saveAppConfiguration(any()) }
    }

    @Test
    fun `Should update cache when data retrieved from API`() = runBlocking {
        val appConfig = mockk<AppConfiguration>()
        every { configurationDb.getAppConfiguration() } returns null
        every { configurationApi.getAppConfiguration() } returns appConfig

        subject.getAppConfiguration()

        verify { configurationApi.getAppConfiguration() }
        verify { configurationDb.saveAppConfiguration(appConfig) }
    }

    @Test
    fun `Should return null when it fails`() = runBlocking {
        every { configurationDb.getAppConfiguration() } returns null
        every { configurationApi.getAppConfiguration() } returns null

        val result = subject.getAppConfiguration()

        verify { configurationApi.getAppConfiguration() }
        verify { configurationDb.getAppConfiguration() }
        assertNull(result)
    }
}
