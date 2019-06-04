package com.jpp.mpdata.datasources.language

interface LanguageDb {
    fun getStoredLanguageString(): String?
    fun updateLanguageString(languageString: String)
}