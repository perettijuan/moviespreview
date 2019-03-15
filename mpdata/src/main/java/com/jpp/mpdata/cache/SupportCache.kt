package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.repository.support.SupportDb

class SupportCache(private val roomDatabase: MPRoomDataBase) : SupportDb {
    override fun clearAllData() {
        roomDatabase.clearAllTables()
    }
}