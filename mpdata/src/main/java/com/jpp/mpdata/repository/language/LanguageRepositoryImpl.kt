package com.jpp.mpdata.repository.language

import com.jpp.mpdata.datasources.language.LanguageDb
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.LanguageRepository

/**
 * [LanguageRepository] implementation.
 */
class LanguageRepositoryImpl(
    private val languageDb: LanguageDb
) : LanguageRepository {

    override suspend fun getCurrentAppLanguage(): SupportedLanguage {
        return languageDb.getStoredLanguageString()?.mapToLanguage()
            ?: SupportedLanguage.English // default language is English
    }

    override suspend fun updateCurrentLanguage(appLanguage: SupportedLanguage) {
        languageDb.updateLanguageString(appLanguage.id)
    }

    private fun String.mapToLanguage(): SupportedLanguage {
        return when (this) {
            SupportedLanguage.Spanish.id -> SupportedLanguage.Spanish
            else -> SupportedLanguage.English
        }
    }
}
