package com.jpp.mp.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
 * and asks the Activity to update the Action Bar's title.
 *
 */
class MainActivityViewModel @Inject constructor(private val languageMonitor: LanguageMonitor,
                                                private val languageRepository: LanguageRepository) : ViewModel() {

    private val viewState by lazy { MutableLiveData<MainActivityViewState>() }

    /**
     * When called, all platform dependent monitoring will start the monitoring process.
     */
    fun onInit() {
        languageRepository.syncPlatformLanguage()
        languageMonitor.startMonitoring()
    }

    override fun onCleared() {
        languageMonitor.stopMonitoring()
    }

    fun viewState(): LiveData<MainActivityViewState> = viewState

    fun userNavigatesToMovieListSection(sectionName: String) {
        viewState.value = MainActivityViewState(
                sectionTitle = sectionName,
                menuBarEnabled = true,
                searchEnabled = false
        )
    }

    fun userNavigatesToMovieDetails(movieTitle: String) {
        navigateToSimpleDestination(movieTitle)
    }

    fun userNavigatesToSearch() {
        viewState.value = MainActivityViewState(
                sectionTitle = "",
                menuBarEnabled = false,
                searchEnabled = true
        )
    }

    fun userNavigatesToCredits(sectionName: String) {
        navigateToSimpleDestination(sectionName)
    }

    fun userNavigatesToPerson(sectionName: String) {
        navigateToSimpleDestination(sectionName)
    }

    fun userNavigatesToAbout(sectionName: String) {
        navigateToSimpleDestination(sectionName)
    }

    fun userNavigatesToLicenses(sectionName: String) {
        navigateToSimpleDestination(sectionName)
    }

    fun userNavigatesToLicenseContent(sectionName: String) {
        navigateToSimpleDestination(sectionName)
    }

    fun userNavigatesToAccountDetails(sectionName: String) {
        navigateToSimpleDestination(sectionName)
    }

    fun userNavigatesWithinFeature(sectionName: String) {
        navigateToSimpleDestination(sectionName)
    }

    /**
     * Update view state when it is navigating to a destination without animation
     * and without menu enabled.
     */
    private fun navigateToSimpleDestination(sectionName: String) {
        viewState.value = MainActivityViewState(
                sectionTitle = sectionName,
                menuBarEnabled = false,
                searchEnabled = false
        )
    }
}