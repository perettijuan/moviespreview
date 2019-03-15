package com.jpp.mpdata.repository.support

import com.jpp.mpdomain.repository.SupportRepository

class SupportRepositoryImpl(private val supportDb: SupportDb) : SupportRepository {
    override fun clearAllData() {
        supportDb.clearAllData()
    }
}