package com.jpp.mpdata.repository.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdata.datasources.language.LanguageDb
import com.jpp.mpdata.datasources.language.LanguageMonitor
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.SupportedLanguage.English
import com.jpp.mpdomain.SupportedLanguage.Spanish
import com.jpp.mpdomain.repository.LanguageRepository
import java.util.Locale

/**
 * [LanguageRepository] implementation.
 */
class LanguageRepositoryImpl(
    private val languageDb: LanguageDb,
    languageMonitor: LanguageMonitor,
    private val locale: LocaleWrapper = LocaleWrapper()
) : LanguageRepository {

    private val stateUpdates = MutableLiveData<SupportedLanguage>()

    init {
        languageMonitor.addListener {
            mapFrom(locale.getDefault()).let {
                languageDb.updateLanguageString(it.id)
                stateUpdates.postValue(it)
            }
        }
    }

    override fun updates(): LiveData<SupportedLanguage> = stateUpdates

    override fun getCurrentAppLanguage(): SupportedLanguage {
        return languageDb.getStoredLanguageString()?.let { mapFrom(locale.localeFrom(it)) }
                ?: mapFrom(locale.getDefault()).also { languageDb.updateLanguageString(it.id) }
    }

    override fun syncPlatformLanguage() {
        mapFrom(locale.getDefault()).let {
            if (it != getCurrentAppLanguage()) {
                languageDb.updateLanguageString(it.id)
            }
        }
    }

    private fun mapFrom(platformLanguage: Locale): SupportedLanguage {
        return when (platformLanguage.language) {
            locale.localeFrom(Spanish.id).language -> Spanish
            else -> English // default is always english.
        }
    }
}
