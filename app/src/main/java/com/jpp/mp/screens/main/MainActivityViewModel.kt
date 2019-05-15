package com.jpp.mp.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jpp.mpdata.datasources.connectivity.ConnectivityMonitor
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
class MainActivityViewModel @Inject constructor(private val connectivityMonitor: ConnectivityMonitor) : ViewModel() {

    private val viewState by lazy { MutableLiveData<MainActivityViewState>() }

    /**
     * When called, all platform dependent monitoring will start the monitoring process.
     */
    fun onInit() {
        connectivityMonitor.startMonitoring()
    }

    override fun onCleared() {
        connectivityMonitor.stopMonitoring()
    }

    fun viewState(): LiveData<MainActivityViewState> = viewState

    fun userNavigatesToMovieListSection(sectionName: String) {
        viewState.postValue(MainActivityViewState.ActionBarLocked(
                abTitle = sectionName,
                withAnimation = viewState.value is MainActivityViewState.ActionBarUnlocked,
                menuEnabled = true,
                isSearch = false)
        )
    }

    fun userNavigatesToMovieDetails(movieTitle: String, contentImageUrl: String) {
        viewState.postValue(MainActivityViewState.ActionBarUnlocked(
                abTitle = movieTitle,
                contentImageUrl = contentImageUrl)
        )
    }

    fun userNavigatesToSearch() {
        viewState.postValue(MainActivityViewState.ActionBarLocked(
                abTitle = "",
                withAnimation = viewState.value is MainActivityViewState.ActionBarUnlocked,
                menuEnabled = false,
                isSearch = true)
        )
    }

    fun userNavigatesToCredits(sectionName: String) {
        navigateToSimpleDestinationWithAnimation(sectionName)
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

    fun userNavigatesToFavoriteMovies(sectionName: String) {
        navigateToSimpleDestinationWithAnimation(sectionName)
    }

    /**
     * Update view state when it is navigating to a destination without animation
     * and without menu enabled.
     */
    private fun navigateToSimpleDestination(sectionName: String) {
        viewState.postValue(MainActivityViewState.ActionBarLocked(
                abTitle = sectionName,
                withAnimation = false,
                menuEnabled = false,
                isSearch = false)
        )
    }

    /**
     * Update view state when it is navigating to a destination with animation
     * and without menu enabled.
     */
    private fun navigateToSimpleDestinationWithAnimation(sectionName: String) {
        viewState.postValue(MainActivityViewState.ActionBarLocked(
                abTitle = sectionName,
                withAnimation = viewState.value is MainActivityViewState.ActionBarUnlocked,
                menuEnabled = false,
                isSearch = false)
        )
    }
}