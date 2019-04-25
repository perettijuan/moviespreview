package com.jpp.mpdata.repository.support

import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.SupportedLanguage.*
import com.jpp.mpdomain.repository.LanguageRepository
import java.util.*

class LanguageRepositoryImpl(private val languageDb: LanguageDb) : LanguageRepository {

    override fun getCurrentDeviceLanguage(): SupportedLanguage {
        return when (Locale.getDefault().language) {
            Locale(Spanish.id).language -> { Spanish }
            else -> English // default is always english.
        }
    }

    override fun getCurrentAppLanguage(): SupportedLanguage? {
        return languageDb.getStoredLanguageString()?.let {
            when (Locale(it).language) {
                Locale(Spanish.id).language -> { Spanish }
                else -> English // default is always english.
            }
        }
    }

    override fun updateAppLanguage(language: SupportedLanguage) {
       languageDb.updateLanguageString(language.id)
    }
}