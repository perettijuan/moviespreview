package com.jpp.moviespreview.datalayer.db.room

import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.ImagesConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RoomModelAdapterTest {

    private lateinit var subject: RoomModelAdapter

    @BeforeEach
    fun setUp() {
        subject = RoomModelAdapter()
    }

    @Test
    fun adaptAppConfigurationToImageSizes() {
        val posterSizes = listOf("p1", "p2", "p3", "p4")
        val profileSizes = listOf("pr1", "pr2", "pr3", "pr4")
        val baseUrl = "baseUrl"
        val appConfiguration = AppConfiguration(ImagesConfiguration(baseUrl, posterSizes, profileSizes))
        val expectedList = listOf(
                DBImageSize(baseUrl, posterSizes[0], 11),
                DBImageSize(baseUrl, posterSizes[1], 11),
                DBImageSize(baseUrl, posterSizes[2], 11),
                DBImageSize(baseUrl, posterSizes[3], 11),
                DBImageSize(baseUrl, profileSizes[0], 22),
                DBImageSize(baseUrl, profileSizes[1], 22),
                DBImageSize(baseUrl, profileSizes[2], 22),
                DBImageSize(baseUrl, profileSizes[3], 22)
        )
        val result = subject.adaptAppConfigurationToImageSizes(appConfiguration)

        assertEquals(expectedList, result)
    }


    @Test
    fun adaptImageSizesToAppConfiguration() {
        val expectedPosterSizes = listOf("p1", "p2", "p3", "p4")
        val expectedProfileSizes = listOf("pr1", "pr2", "pr3", "pr4")
        val expectedBaseUrl = "baseUrl"
        val dBImageSizeList = listOf(
                DBImageSize(expectedBaseUrl, expectedPosterSizes[0], 11),
                DBImageSize(expectedBaseUrl, expectedPosterSizes[1], 11),
                DBImageSize(expectedBaseUrl, expectedPosterSizes[2], 11),
                DBImageSize(expectedBaseUrl, expectedPosterSizes[3], 11),
                DBImageSize(expectedBaseUrl, expectedProfileSizes[0], 22),
                DBImageSize(expectedBaseUrl, expectedProfileSizes[1], 22),
                DBImageSize(expectedBaseUrl, expectedProfileSizes[2], 22),
                DBImageSize(expectedBaseUrl, expectedProfileSizes[3], 22)
        )
        val appConfig = subject.adaptImageSizesToAppConfiguration(dBImageSizeList)

        assertEquals(expectedBaseUrl, appConfig.images.base_url)
        assertEquals(expectedPosterSizes, appConfig.images.poster_sizes)
        assertEquals(expectedProfileSizes, appConfig.images.profile_sizes)
    }
}