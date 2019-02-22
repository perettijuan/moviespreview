package com.jpp.mpdata.repository.person

import com.jpp.mpdomain.Person

interface PersonApi {
    fun getPerson(personId: Double): Person?
}