package com.jpp.mpdata.repository.person

import com.jpp.mpdomain.Person
import com.jpp.mpdomain.SupportedLanguage

interface PersonApi {
    fun getPerson(personId: Double, language: SupportedLanguage): Person?
}
