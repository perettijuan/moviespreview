package com.jpp.moviespreview.datalayer.db.repository

import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.db.MPDatabase
import com.jpp.moviespreview.datalayer.db.cache.MPCache
import io.mockk.every
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
class DBConfigurationRepositoryTest {


    @RelaxedMockK
    private lateinit var mpCache: MPCache
    @RelaxedMockK
    private lateinit var mpDatabase: MPDatabase

    private lateinit var subject: DBConfigurationRepository

    @BeforeEach
    fun setUp() {
        subject = DBConfigurationRepository(mpCache, mpDatabase)
    }

    @Test
    @DisplayName("Should retrieve data from DB when cache is valid ")
    fun getConfiguration_whenDataIsCached() {
        val expected = mockk<AppConfiguration>()

        every { mpCache.isAppConfigurationUpToDate() } returns true
        every { mpDatabase.getStoredAppConfiguration() } returns expected

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
        val appConfigMock = mockk<AppConfiguration>()

        subject.updateAppConfiguration(appConfigMock)

        verify { mpDatabase.updateAppConfiguration(appConfigMock) }
        verify { mpCache.updateAppConfigurationInserted() }
    }

}