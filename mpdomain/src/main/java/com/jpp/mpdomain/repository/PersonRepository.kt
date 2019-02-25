package com.jpp.mpdomain.repository

import com.jpp.mpdomain.Person

/**
 * Repository definition to retrieve a [Person] whenever is possible.
 */
interface PersonRepository {
    /**
     * Retrieves the person that is identified by [personId].
     */
    fun getPerson(personId: Double) : Person?
}