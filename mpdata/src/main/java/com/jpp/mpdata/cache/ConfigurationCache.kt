package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.ImageSizeDAO
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdata.cache.timestamps.MPTimestamps
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.repository.configuration.ConfigurationDb

class ConfigurationCache(private val timestamps: MPTimestamps,
                         private val roomDatabase: MPRoomDataBase,
                         private val adapter: RoomModelAdapter) : ConfigurationDb {

    override fun getAppConfiguration(): AppConfiguration? {
        return when (timestamps.isAppConfigurationUpToDate()) {
            true -> withImageSizeDAO { getImageSizes() }
                    ?.let { transformWithAdapter { adaptImageSizesToAppConfiguration(it) } }
            else -> null
        }
    }

    override fun saveAppConfiguration(appConfiguration: AppConfiguration) {
        withImageSizeDAO {
            insertImageSizes(transformWithAdapter { adaptAppConfigurationToImageSizes(appConfiguration) })
        }.run {
            timestamps.updateAppConfigurationInserted()
        }
    }

    private fun <T> transformWithAdapter(action: RoomModelAdapter.() -> T): T = with(adapter) { action.invoke(this) }

    private fun <T> withImageSizeDAO(action: ImageSizeDAO.() -> T): T = with(roomDatabase.imageSizeDao()) { action.invoke(this) }
}
