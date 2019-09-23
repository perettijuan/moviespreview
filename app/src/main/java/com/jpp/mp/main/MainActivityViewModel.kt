package com.jpp.mp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.navigation.Destination
import com.jpp.mp.common.navigation.Destination.*
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
class MainActivityViewModel @Inject constructor(private val languageMonitor: LanguageMonitor,
                                                private val languageRepository: LanguageRepository) : ViewModel() {

    private val _viewState = MutableLiveData<MainActivityViewState>()
    val viewState: LiveData<MainActivityViewState> get() = _viewState

    private val _moduleNavEvents = SingleLiveEvent<ModuleNavigationEvent>()
    val moduleNavEvents: LiveData<ModuleNavigationEvent> get() = _moduleNavEvents

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

    /**
     * Called when the user requests to navigate to a particular [destination].
     */
    fun onRequestToNavigateToDestination(destination: Destination) {
        _moduleNavEvents.value = when (destination) {
            is MPAccount -> ModuleNavigationEvent.NavigateToNodeWithId.toUserAccount()
            is MPMovieDetails -> ModuleNavigationEvent.NavigateToNodeWithExtras.toMovieDetails(destination)
            is MPPerson -> ModuleNavigationEvent.NavigateToNodeWithExtras.toPerson(destination)
            is MPCredits -> ModuleNavigationEvent.NavigateToNodeWithExtras.toCredits(destination)
            is PreviousDestination -> ModuleNavigationEvent.NavigateToPrevious
            is InnerDestination -> ModuleNavigationEvent.NavigateToNodeWithDirections(destination.directions)
            else -> throw IllegalStateException("Unknown navigation requested $destination")
        }
    }

    /**
     * Called when a new [Destination] is reached.
     */
    fun onDestinationReached(reachedDestination: Destination) {
        when (reachedDestination) {
            is ReachedDestination -> renderFeatureViewState(reachedDestination.destinationTitle)
            is MovieListReached -> renderMovieListViewState(reachedDestination.title)
            is MPSearch -> renderSearchViewState()
            is MPCredits -> renderFeatureViewState(reachedDestination.movieTitle)
            else -> throw IllegalStateException("Unknown navigation requested $reachedDestination")
        }
    }

    /**
     * Called when the user navigates to the search feature.
     * When called, this method will post a new [MainActivityViewState]
     * to update the search bar state.
     */
    private fun renderSearchViewState() {
        _viewState.value = MainActivityViewState(
                sectionTitle = "",
                menuBarEnabled = false,
                searchEnabled = true
        )
    }

    /**
     * Called when the user navigates internally in a feature module.
     * When called, this method will post a new [MainActivityViewState] that
     * contains the title of the feature being navigated.
     */
    private fun renderFeatureViewState(sectionName: String) {
        _viewState.value = MainActivityViewState(
                sectionTitle = sectionName,
                menuBarEnabled = false,
                searchEnabled = false
        )
    }

    /**
     * Called when the user navigates to a particular movie list section.
     * When called, it will post a new [MainActivityViewState] that contains
     * the name of the section being shown.
     */
    private fun renderMovieListViewState(sectionName: String) {
        _viewState.value = MainActivityViewState(
                sectionTitle = sectionName,
                menuBarEnabled = true,
                searchEnabled = false
        )
    }
}