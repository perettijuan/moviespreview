package com.jpp.moviespreview.screens.main

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
                searchEnabled = true)
        )
    }

    fun userNavigatesToMovieDetails(movieTitle: String, contentImageUrl: String) {
        viewState.postValue(MainActivityViewState.ActionBarUnlocked(
                abTitle = movieTitle,
                contentImageUrl = contentImageUrl)
        )
    }

    fun userNavigatesToSearch() {
        viewState.postValue(MainActivityViewState.SearchEnabled(
                withAnimation = viewState.value is MainActivityViewState.ActionBarUnlocked)
        )
    }

    fun userNavigatesToPerson(sectionName: String) {
        viewState.postValue(MainActivityViewState.ActionBarLocked(
                abTitle = sectionName,
                withAnimation = false,
                searchEnabled = false)
        )
    }

    fun userNavigatesToCredits(sectionName: String) {
        viewState.postValue(MainActivityViewState.ActionBarLocked(
                abTitle = sectionName,
                withAnimation = true,
                searchEnabled = false)
        )
    }
}