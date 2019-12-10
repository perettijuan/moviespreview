package com.jpp.mpdata.datasources.language

/**
 * Database definition to manipulate all the languages data locally.
 */
interface LanguageDb {
    /**
     * @return a String value that contains the representation of the unique language
     * stored locally.
     */
    fun getStoredLanguageString(): String?

    /**
     * Updates the unique stored language representation with the provided [languageString].
     */
    fun updateLanguageString(languageString: String)
}
