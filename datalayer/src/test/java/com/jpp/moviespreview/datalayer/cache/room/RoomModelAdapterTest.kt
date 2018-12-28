package com.jpp.moviespreview.datalayer.cache.room

import com.jpp.moviespreview.domainlayer.AppConfiguration
import com.jpp.moviespreview.domainlayer.ImagesConfiguration
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
        val backdropSizes = listOf("br1", "br2", "br3", "br4")
        val baseUrl = "baseUrl"
        val appConfiguration = AppConfiguration(ImagesConfiguration(baseUrl, posterSizes, profileSizes, backdropSizes))
        val expectedList = listOf(
                DBImageSize(baseUrl, posterSizes[0], 11),
                DBImageSize(baseUrl, posterSizes[1], 11),
                DBImageSize(baseUrl, posterSizes[2], 11),
                DBImageSize(baseUrl, posterSizes[3], 11),
                DBImageSize(baseUrl, profileSizes[0], 22),
                DBImageSize(baseUrl, profileSizes[1], 22),
                DBImageSize(baseUrl, profileSizes[2], 22),
                DBImageSize(baseUrl, profileSizes[3], 22),
                DBImageSize(baseUrl, backdropSizes[0], 33),
                DBImageSize(baseUrl, backdropSizes[1], 33),
                DBImageSize(baseUrl, backdropSizes[2], 33),
                DBImageSize(baseUrl, backdropSizes[3], 33)
        )
        val result = subject.adaptAppConfigurationToImageSizes(appConfiguration)

        assertEquals(expectedList, result)
    }


    @Test
    fun adaptImageSizesToAppConfiguration() {
        val expectedPosterSizes = listOf("p1", "p2", "p3", "p4")
        val expectedProfileSizes = listOf("pr1", "pr2", "pr3", "pr4")
        val backdropSizes = listOf("br1", "br2", "br3", "br4")
        val expectedBaseUrl = "baseUrl"
        val dBImageSizeList = listOf(
                DBImageSize(expectedBaseUrl, expectedPosterSizes[0], 11),
                DBImageSize(expectedBaseUrl, expectedPosterSizes[1], 11),
                DBImageSize(expectedBaseUrl, expectedPosterSizes[2], 11),
                DBImageSize(expectedBaseUrl, expectedPosterSizes[3], 11),
                DBImageSize(expectedBaseUrl, expectedProfileSizes[0], 22),
                DBImageSize(expectedBaseUrl, expectedProfileSizes[1], 22),
                DBImageSize(expectedBaseUrl, expectedProfileSizes[2], 22),
                DBImageSize(expectedBaseUrl, expectedProfileSizes[3], 22),
                DBImageSize(expectedBaseUrl, backdropSizes[0], 33),
                DBImageSize(expectedBaseUrl, backdropSizes[1], 33),
                DBImageSize(expectedBaseUrl, backdropSizes[2], 33),
                DBImageSize(expectedBaseUrl, backdropSizes[3], 33)
        )
        val appConfig = subject.adaptImageSizesToAppConfiguration(dBImageSizeList)

        assertEquals(expectedBaseUrl, appConfig.images.base_url)
        assertEquals(expectedPosterSizes, appConfig.images.poster_sizes)
        assertEquals(expectedProfileSizes, appConfig.images.profile_sizes)
        assertEquals(backdropSizes, appConfig.images.backdrop_sizes)
    }
}