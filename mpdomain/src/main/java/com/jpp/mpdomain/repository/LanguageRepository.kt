package com.jpp.mpdomain.repository

import com.jpp.mpdomain.SupportedLanguage

interface LanguageRepository {
    /**
     * @return the [SupportedLanguage] that the device is currently using.
     */
    fun getCurrentDeviceLanguage() : SupportedLanguage

    /**
     * @return the [SupportedLanguage] that the application is currently configured with.
     */
    fun getCurrentAppLanguage() : SupportedLanguage?

    /**
     * Updates the [SupportedLanguage] that the application is configured with.
     */
    fun updateAppLanguage(language: SupportedLanguage)
}