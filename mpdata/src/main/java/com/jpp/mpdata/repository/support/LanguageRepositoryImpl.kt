package com.jpp.mpdata.repository.support

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.SupportedLanguage.*
import com.jpp.mpdomain.repository.LanguageRepository
import java.util.*

class LanguageRepositoryImpl(private val languageDb: LanguageDb,
                             private val context: Context) : LanguageRepository {

    private val stateUpdates by lazy { MutableLiveData<LanguageRepository.LanguageEvent>() }
    private val languageReceiver = LanguageReceiver {
        stateUpdates.postValue(LanguageRepository.LanguageEvent.LanguageChangeEvent)
    }

    init {
        val iFilter = IntentFilter(Intent.ACTION_LOCALE_CHANGED)
        context.registerReceiver(languageReceiver, iFilter)
    }

    override fun updates(): LiveData<LanguageRepository.LanguageEvent> = stateUpdates

    override fun getCurrentDeviceLanguage(): SupportedLanguage {
        return when (Locale.getDefault().language) {
            Locale(Spanish.id).language -> Spanish
            else -> English // default is always english.
        }
    }

    override fun getCurrentAppLanguage(): SupportedLanguage? {
        return languageDb.getStoredLanguageString()?.let {
            when (Locale(it).language) {
                Locale(Spanish.id).language -> Spanish
                else -> English // default is always english.
            }
        }
    }

    override fun updateAppLanguage(language: SupportedLanguage) {
        languageDb.updateLanguageString(language.id)
    }

    private class LanguageReceiver(private val callback: () -> Unit) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            callback()
        }
    }
}