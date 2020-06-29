package com.jpp.mpdata.repository.person

import com.jpp.mpdomain.Person
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.PersonRepository

class PersonRepositoryImpl(
    private val personApi: PersonApi,
    private val personDb: PersonDb
) : PersonRepository {
    override suspend fun getPerson(personId: Double, language: SupportedLanguage): Person? {
        return personDb.getPerson(personId) ?: run {
            personApi.getPerson(personId, language)?.also { personDb.savePerson(it) }
        }
    }

    override suspend fun flushPersonData() {
        personDb.clearAllData()
    }
}
