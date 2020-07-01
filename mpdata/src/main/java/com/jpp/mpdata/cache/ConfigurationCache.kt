package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomDomainAdapter
import com.jpp.mpdata.datasources.configuration.ConfigurationDb
import com.jpp.mpdomain.AppConfiguration

/**
 * [ConfigurationDb] implementation with a cache mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class ConfigurationCache(
    roomDatabase: MPRoomDataBase,
    private val toDomain: RoomDomainAdapter,
    private val toRoom: DomainRoomAdapter,
    private val timestamp: CacheTimestampHelper
) : ConfigurationDb {

    private val imageSizeDao = roomDatabase.imageSizeDao()

    override fun getAppConfiguration(): AppConfiguration? {
        val dbImageSizes = imageSizeDao.getImageSizes(timestamp.now())
        return if (dbImageSizes.isNotEmpty()) {
            toDomain.adaptImageSizesToAppConfiguration(dbImageSizes)
        } else {
            null
        }
    }

    override fun saveAppConfiguration(appConfiguration: AppConfiguration) {
        val dbImageSizes = toRoom.imageSizes(appConfiguration, timestamp.appConfigDueDate())
        imageSizeDao.insertImageSizes(dbImageSizes)
    }

    /**
     * @return a Long that represents the expiration date of the configuration data stored in the
     * device.
     */
    private fun CacheTimestampHelper.appConfigDueDate() = now() + appConfigRefreshTime()
}
