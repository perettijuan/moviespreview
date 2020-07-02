package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.datasources.support.SupportDb

/**
 * [SupportDb] implementation to access the data stored in the device.
 */
class SupportCache(private val roomDatabase: MPRoomDataBase) :
    SupportDb {
    override fun clearAllData() {
        roomDatabase.clearAllTables()
    }
}
