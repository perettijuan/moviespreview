package com.jpp.mpdomain.repository

import com.jpp.mpdomain.SupportedLanguage

/**
 * Repository definition to handle data access to [SupportedLanguage].
 */
interface LanguageRepository {

    /**
     * Called in order to synchronize the stored language with the language currently
     * used in the application.
     */
    suspend fun updateCurrentLanguage(appLanguage: SupportedLanguage)

    /**
     * @return the [SupportedLanguage] that the application is currently configured with.
     */
    suspend fun getCurrentAppLanguage(): SupportedLanguage
}
