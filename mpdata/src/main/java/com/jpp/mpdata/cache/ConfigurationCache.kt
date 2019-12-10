package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.ImageSizeDAO
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdata.datasources.configuration.ConfigurationDb
import com.jpp.mpdomain.AppConfiguration

/**
 * [ConfigurationDb] implementation with a cache mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class ConfigurationCache(
    private val roomDatabase: MPRoomDataBase,
    private val adapter: RoomModelAdapter,
    private val timestampHelper: CacheTimestampHelper
) : ConfigurationDb {

    override fun getAppConfiguration(): AppConfiguration? {
        return withImageSizeDAO { getImageSizes(now()) }
                .let {
                    when (it.isNotEmpty()) {
                        true -> { transformWithAdapter { adaptImageSizesToAppConfiguration(it) } }
                        false -> null
                    }
                }
    }

    override fun saveAppConfiguration(appConfiguration: AppConfiguration) {
        withImageSizeDAO {
            insertImageSizes(transformWithAdapter { adaptAppConfigurationToImageSizes(appConfiguration, appConfigRefreshTime()) })
        }
    }

    /**
     * Helper function to execute a [transformation] in with the [RoomModelAdapter] instance.
     */
    private fun <T> transformWithAdapter(transformation: RoomModelAdapter.() -> T): T = with(adapter) { transformation.invoke(this) }

    /**
     * Helper function to execute an [action] with the [ImageSizeDAO] instance obtained from [MPRoomDataBase].
     */
    private fun <T> withImageSizeDAO(action: ImageSizeDAO.() -> T): T = with(roomDatabase.imageSizeDao()) { action.invoke(this) }

    /**
     * @return a Long that represents the current time.
     */
    private fun now() = timestampHelper.now()

    /**
     * @return a Long that represents the expiration date of the configuration data stored in the
     * device.
     */
    private fun appConfigRefreshTime() = with(timestampHelper) { now() + appConfigRefreshTime() }
}
