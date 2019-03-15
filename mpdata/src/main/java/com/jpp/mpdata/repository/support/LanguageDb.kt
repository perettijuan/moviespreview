package com.jpp.mpdata.repository.support

interface LanguageDb {
    fun getStoredLanguageString(): String?
    fun updateLanguageString(languageString: String)
}