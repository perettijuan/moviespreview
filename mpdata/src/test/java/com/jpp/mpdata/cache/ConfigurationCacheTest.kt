package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.DBImageSize
import com.jpp.mpdata.cache.room.ImageSizeDAO
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomDomainAdapter
import com.jpp.mpdomain.AppConfiguration
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ConfigurationCacheTest {

    @RelaxedMockK
    private lateinit var imageSizeDAO: ImageSizeDAO
    @MockK
    private lateinit var roomModelAdapter: RoomDomainAdapter
    @MockK
    private lateinit var timestampHelper: CacheTimestampHelper

    private lateinit var subject: ConfigurationCache

    @BeforeEach
    fun setUp() {
        val roomDatabase = mockk<MPRoomDataBase>()
        every { roomDatabase.imageSizeDao() } returns imageSizeDAO
        subject = ConfigurationCache(roomDatabase, roomModelAdapter, timestampHelper)
    }

    @Test
    fun `Should return null when there is configuration stored`() {
        val now = 12L

        every { timestampHelper.now() } returns now
        every { imageSizeDAO.getImageSizes(now) } returns listOf() // empty list means no data stored

        val result = subject.getAppConfiguration()

        assertNull(result)
    }

    @Test
    fun `Should return app configuration mapped from image list`() {
        val now = 12L
        val dbImageSizes = listOf<DBImageSize>(mockk(), mockk(), mockk())
        val appConfiguration = mockk<AppConfiguration>()

        every { timestampHelper.now() } returns now
        every { imageSizeDAO.getImageSizes(now) } returns dbImageSizes
        every { roomModelAdapter.adaptImageSizesToAppConfiguration(any()) } returns appConfiguration

        val result = subject.getAppConfiguration()

        assertEquals(result, appConfiguration)
        verify { roomModelAdapter.adaptImageSizesToAppConfiguration(dbImageSizes) }
    }

    @Test
    fun `Should insert movie page and movie list when saving movie page`() {
        val appConfiguration = mockk<AppConfiguration>()
        val dbImageSizes = mockk<List<DBImageSize>>()
        val now = 12L
        val movieRefreshTime = 10L
        val expectedDueDate = 22L

        every { timestampHelper.now() } returns now
        every { timestampHelper.appConfigRefreshTime() } returns movieRefreshTime
        every { roomModelAdapter.adaptAppConfigurationToImageSizes(any(), any()) } returns dbImageSizes

        subject.saveAppConfiguration(appConfiguration)

        verify { roomModelAdapter.adaptAppConfigurationToImageSizes(appConfiguration, expectedDueDate) }
        verify { imageSizeDAO.insertImageSizes(dbImageSizes) }
    }
}
