package com.jpp.mp.main

import androidx.lifecycle.ViewModel
import com.jpp.mpdata.datasources.language.LanguageMonitor
import com.jpp.mpdomain.repository.LanguageRepository
import javax.inject.Inject

/**
 * ViewModel used by [MainActivity].
 *
 * Core responsibilities:
 *
 * 1 - Some features implemented in the application requires monitoring some particular APIs of the platform.
 * Since the application's architecture has only one Activity implemented ([MainActivity]), the ViewModel
 * that supports that Activity is the perfect place to start/stop the monitoring of platform dependent
 * APIs. That's a responsibility of the ViewModel.
 *
 * 2 - The application is using the navigation architecture components, with the caveat that needs to
 * show dynamic titles in the Action Bar. This VM takes care of verifying the navigation events
 * and asks the Activity to update the Action Bar's movieTitle.
 *
 */
class MainActivityViewModel @Inject constructor(
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
