package com.jpp.mpdomain.repository

import com.jpp.mpdomain.Person
import com.jpp.mpdomain.SupportedLanguage

/**
 * Repository definition to retrieve a [Person] whenever is possible.
 */
interface PersonRepository {
    /**
     * Retrieves the person that is identified by [personId].
     */
    suspend fun getPerson(personId: Double, language: SupportedLanguage): Person?

    /**
     * Flushes out any stored data.
     */
    suspend fun flushPersonData()
}
