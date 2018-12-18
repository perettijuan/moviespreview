package com.jpp.moviespreview.datalayer.cache.repository

import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.DataModelMapper
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.domainlayer.ImagesConfiguration
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CacheConfigurationRepositoryTest {


    @RelaxedMockK
    private lateinit var mpCache: MPTimestamps
    @RelaxedMockK
    private lateinit var mpDatabase: MPDataBase
    @MockK
    private lateinit var mapper: DataModelMapper

    private lateinit var subject: CacheConfigurationRepository


    @BeforeEach
    fun setUp() {
        subject = CacheConfigurationRepository(mpCache, mpDatabase, mapper)
    }

    @Test
    @DisplayName("Should retrieve data from DB when cache is valid ")
    fun getConfiguration_whenDataIsCached() {
        val dataAppConfiguration = mockk<AppConfiguration>()
        val expected = mockk<ImagesConfiguration>()

        every { mpCache.isAppConfigurationUpToDate() } returns true
        every { mpDatabase.getStoredAppConfiguration() } returns dataAppConfiguration
        every { mapper.mapDataAppConfiguration(dataAppConfiguration) } returns expected

        val actual = subject.getConfiguration()

        assertEquals(expected, actual)

        verify { mpDatabase.getStoredAppConfiguration() }
    }

    @Test
    @DisplayName("Should return null when data in cache is out of date ")
    fun getConfiguration_whenDataInCacheIsOutdated() {
        val expected = mockk<AppConfiguration>()

        every { mpCache.isAppConfigurationUpToDate() } returns false
        every { mpDatabase.getStoredAppConfiguration() } returns expected

        val actual = subject.getConfiguration()

        assertNull(actual)

        verify(exactly = 0) { mpDatabase.getStoredAppConfiguration() }
    }

    @Test
    @DisplayName("Should update database and cache when updating app configuration")
    fun updateAppConfiguration() {
        val domainImagesConfigMock = mockk<ImagesConfiguration>()
        val appConfigMock = mockk<AppConfiguration>()

        every { mapper.mapDomainImagesConfiguration(domainImagesConfigMock) } returns appConfigMock

        subject.updateAppConfiguration(domainImagesConfigMock)

        verify { mpDatabase.updateAppConfiguration(appConfigMock) }
        verify { mpCache.updateAppConfigurationInserted() }
    }

}