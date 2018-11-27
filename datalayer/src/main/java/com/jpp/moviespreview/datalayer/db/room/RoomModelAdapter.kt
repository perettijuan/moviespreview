package com.jpp.moviespreview.datalayer.db.room

import com.jpp.moviespreview.common.extensions.addAllMapping
import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.ImagesConfiguration


/**
 * Adapts the Room data model to the data layer module (and viceversa).
 */
class RoomModelAdapter {


    private companion object {
        sealed class ImageTypes(val id: Int) {
            object PosterType : ImageTypes(11)
            object ProfileType : ImageTypes(22)
        }
    }

    /**
     * Adapts the provided [appConfiguration] to a list of [DBImageSize].
     */
    fun adaptAppConfigurationToImageSizes(appConfiguration: AppConfiguration): List<DBImageSize> {
        return appConfiguration.images.poster_sizes
                .map { DBImageSize(appConfiguration.images.base_url, it, ImageTypes.PosterType.id) }
                .toMutableList()
                .addAllMapping {
                    appConfiguration.images.profile_sizes.map { posterSize ->
                        DBImageSize(appConfiguration.images.base_url, posterSize, ImageTypes.ProfileType.id)
                    }
                }
    }

    /**
     * Creates an [AppConfiguration] instance using the provided [dbSizes].
     */
    fun adaptImageSizesToAppConfiguration(dbSizes: List<DBImageSize>): AppConfiguration {
        return AppConfiguration(ImagesConfiguration(
                dbSizes[0].baseUrl,
                dbSizes
                        .filter { it.imageType == ImageTypes.PosterType.id }
                        .map { it.size },
                dbSizes
                        .filter { it.imageType == ImageTypes.ProfileType.id }
                        .map { it.size }
        ))
    }
}