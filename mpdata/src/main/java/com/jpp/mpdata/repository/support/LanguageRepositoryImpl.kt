package com.jpp.mpdata.repository.support

import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.SupportedLanguage.*
import com.jpp.mpdomain.repository.LanguageRepository
import java.util.*

class LanguageRepositoryImpl : LanguageRepository {
    override fun getCurrentAppLanguage(): SupportedLanguage {
        return when (Locale.getDefault().language) {
            Locale(Spanish.id).language -> { Spanish }
            else -> English // default is always english.
        }
    }
}