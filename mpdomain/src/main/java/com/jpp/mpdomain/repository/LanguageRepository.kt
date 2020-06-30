package com.jpp.mpdomain.repository

import androidx.lifecycle.LiveData
import com.jpp.mpdomain.SupportedLanguage

/**
 * Repository definition to handle data access to [SupportedLanguage].
 */
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
    suspend fun syncPlatformLanguage()

    /**
     * @return the [SupportedLanguage] that the application is currently configured with.
     */
    suspend fun getCurrentAppLanguage(): SupportedLanguage
}
