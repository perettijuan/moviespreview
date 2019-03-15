package com.jpp.mpdata.repository.person

import com.jpp.mpdomain.Person

interface PersonDb {
    fun getPerson(personId: Double): Person?
    fun savePerson(person: Person)
    fun clearAllData()


    /**
     * Persons are not stored in the local storage. Instead, they're kept in memory
     */
    class Impl : PersonDb {
        private val persons = HashMap<Double, Person>()
        override fun getPerson(personId: Double): Person? = persons[personId]
        override fun savePerson(person: Person) {
            persons[person.id] = person
        }
        override fun clearAllData() {
            persons.clear()
        }
    }
}