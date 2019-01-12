package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.ImageSizeDAO
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.repository.configuration.ConfigurationDb

/**
 * [ConfigurationDb] implementation with a cache mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class ConfigurationCache(private val roomDatabase: MPRoomDataBase,
                         private val adapter: RoomModelAdapter,
                         private val timestampHelper: CacheTimestampHelper) : ConfigurationDb {

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

    private fun <T> transformWithAdapter(action: RoomModelAdapter.() -> T): T = with(adapter) { action.invoke(this) }

    private fun <T> withImageSizeDAO(action: ImageSizeDAO.() -> T): T = with(roomDatabase.imageSizeDao()) { action.invoke(this) }

    private fun now() = timestampHelper.now()

    private fun appConfigRefreshTime() = with(timestampHelper) { now() + appConfigRefreshTime() }
}
