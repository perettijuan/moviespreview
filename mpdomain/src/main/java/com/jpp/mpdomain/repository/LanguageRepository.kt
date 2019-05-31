package com.jpp.mpdomain.repository

import androidx.lifecycle.LiveData
import com.jpp.mpdomain.SupportedLanguage

interface LanguageRepository {
    /**
     * Subscribe to the [LiveData] whenever you need to update the state based on the data
     * that is being handled by this repository.
     */
    fun updates(): LiveData<SupportedLanguage>

    /**
     * Called in order to synchronize the stored language with the language currently
     * used in the application.
     */
    fun syncPlatformLanguage()

    /**
     * @return the [SupportedLanguage] that the application is currently configured with.
     */
    fun getCurrentAppLanguage(): SupportedLanguage
}