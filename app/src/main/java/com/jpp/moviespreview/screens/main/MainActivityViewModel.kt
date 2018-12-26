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

    private val mainActivityViewState by lazy { MutableLiveData<MainActivityViewState>() }

    fun bindViewState(): LiveData<MainActivityViewState> = mainActivityViewState

    fun onAction(action: MainActivityAction) {
        when (action) {
            is MainActivityAction.UserSelectedMovieDetails -> mainActivityViewState.postValue(MainActivityViewState.ActionBarUnlocked(action.movieImageUrl))
            MainActivityAction.UserSelectedMovieList -> mainActivityViewState.postValue(MainActivityViewState.ActionBarLocked)
        }
    }
}