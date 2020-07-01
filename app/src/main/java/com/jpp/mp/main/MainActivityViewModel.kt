package com.jpp.mp.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mpdata.datasources.language.LanguageMonitor
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.SupportRepository
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel used by [MainActivity].
 *
 */
class MainActivityViewModel(
    private val languageMonitor: LanguageMonitor,
    private val languageRepository: LanguageRepository,
    private val supportRepository: SupportRepository,
    private val locale: LocaleWrapper,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    /**
     * Called on application startup. When called, the VM takes care of initializing
     * the components that are needed by the application to work properly.
     * When [onCleared] is called, the monitoring will be stopped.
     */
    fun onInit() {
        syncAppLanguage()
        languageMonitor.startMonitoring()
        languageMonitor.addListener { syncAppLanguage() }
    }

    /**
     * Lifecycle method overridden to stop all executions started in [onInit]
     */
    override fun onCleared() {
        languageMonitor.stopMonitoring()
    }

    private fun syncAppLanguage() {
        viewModelScope.launch {
            val platformLanguage = locale.getDefault().mapToSupportedLanguage()
            val appLanguage = withContext(ioDispatcher) {
                languageRepository.getCurrentAppLanguage()
            }

            if (platformLanguage != appLanguage) {
                withContext(ioDispatcher) {
                    supportRepository.clearAllData()
                    languageRepository.updateCurrentLanguage(platformLanguage)
                }
            }
        }
    }

    private fun Locale.mapToSupportedLanguage(): SupportedLanguage {
        return when (language) {
            locale.localeFrom(SupportedLanguage.Spanish.id).language -> SupportedLanguage.Spanish
            else -> SupportedLanguage.English
        }
    }
}
