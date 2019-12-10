package com.jpp.mpdata.repository.support

import com.jpp.mpdata.repository.person.PersonDb
import com.jpp.mpdomain.repository.SupportRepository

class SupportRepositoryImpl(
    private val supportDb: SupportDb,
    private val personDb: PersonDb
) : SupportRepository {
    override fun clearAllData() {
        supportDb.clearAllData()
        personDb.clearAllData()
    }
}
