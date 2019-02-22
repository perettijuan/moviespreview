package com.jpp.mpdata.repository.person

import com.jpp.mpdomain.Person
import com.jpp.mpdomain.repository.PersonRepository

class PersonRepositoryImpl(private val personApi: PersonApi,
                           private val personDb: PersonDb) : PersonRepository {
    override fun getPerson(personId: Double): Person? {
        return personDb.getPerson(personId) ?: run {
            personApi.getPerson(personId)?.also { personDb.savePerson(it) }
        }
    }
}