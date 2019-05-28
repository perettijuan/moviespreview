package com.jpp.mpdomain.repository

import androidx.lifecycle.LiveData
import com.jpp.mpdomain.SupportedLanguage

interface LanguageRepository {

    /**
     * Object that represents the event of language change.
     */
    sealed class LanguageEvent {
        object LanguageChangeEvent : LanguageEvent()
    }

    /**
     * Subscribe to the [LiveData] whenever you need to update the state based on the data
     * that is being handled by this repository.
     */
    fun updates(): LiveData<LanguageEvent>

    /**
     * @return the [SupportedLanguage] that the device is currently using.
     */
    fun getCurrentDeviceLanguage(): SupportedLanguage

    /**
     * @return the [SupportedLanguage] that the application is currently configured with.
     */
    fun getCurrentAppLanguage(): SupportedLanguage

    /**
     * Updates the [SupportedLanguage] that the application is configured with.
     */
    fun updateAppLanguage(language: SupportedLanguage)
}