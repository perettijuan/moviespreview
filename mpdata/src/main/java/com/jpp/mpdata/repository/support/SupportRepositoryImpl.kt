package com.jpp.mpdata.repository.support

import com.jpp.mpdata.datasources.person.PersonDb
import com.jpp.mpdata.datasources.support.SupportDb
import com.jpp.mpdomain.repository.SupportRepository

class SupportRepositoryImpl(
    private val supportDb: SupportDb,
    private val personDb: PersonDb
) : SupportRepository {
    override suspend fun clearAllData() {
        supportDb.clearAllData()
        personDb.clearAllData()
    }
}
