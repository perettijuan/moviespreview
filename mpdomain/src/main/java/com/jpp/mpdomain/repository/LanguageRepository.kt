package com.jpp.mpdomain.repository

import com.jpp.mpdomain.SupportedLanguage

interface LanguageRepository {
    /**
     * @return the current [SupportedLanguage] that the application has (equivalent to the
     * language that the device has configured currently).
     */
    fun getCurrentAppLanguage() : SupportedLanguage
}