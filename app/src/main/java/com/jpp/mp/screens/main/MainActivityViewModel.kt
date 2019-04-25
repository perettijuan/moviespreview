package com.jpp.mp.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * This is the ViewModel that backs the behavior supported by the MainActivity.
 * It is a shared ViewModel: all Fragments that needs to update the MainActivity
 * UI can access this ViewModel (since all Fragments are hosted by the MainActivity and this
 * ViewModel updates the MainActivity UI) and do the things that they require.
 */
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val viewState by lazy { MutableLiveData<MainActivityViewState>() }

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