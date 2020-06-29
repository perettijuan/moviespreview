package com.jpp.mp.main

import androidx.lifecycle.ViewModel
import com.jpp.mpdata.datasources.language.LanguageMonitor
import com.jpp.mpdomain.repository.LanguageRepository

/**
 * ViewModel used by [MainActivity].
 *
 */
class MainActivityViewModel(
    private val languageMonitor: LanguageMonitor,
    private val languageRepository: LanguageRepository
) : ViewModel() {

    /**
     * Called on application startup. When called, the VM takes care of initializing
     * the components that are needed by the application to work properly.
     * When [onCleared] is called, the monitoring will be stopped.
     */
    fun onInit() {
        languageRepository.syncPlatformLanguage()
        languageMonitor.startMonitoring()
    }

    /**
     * Lifecycle method overridden to stop all executions started in [onInit]
     */
    override fun onCleared() {
        languageMonitor.stopMonitoring()
    }
}
