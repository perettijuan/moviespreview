package com.jpp.mp.common.navigation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.jpp.mp.common.navigation.InterModuleNavigationEvent.Companion.of
import javax.inject.Inject

/**
 * Provides functionality to perform inter-modules navigation.
 * The application is organized in different feature-modules and uses the Navigation Architecture
 * library from Architecture Components. The problem this ViewModel solves is how to navigate
 * from a feature-module A to a feature-module B without adding a dependency to the feature-module A.
 */
//TODO JPP add tests
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _navEvents by lazy { MutableLiveData<InterModuleNavigationEvent>() }


    /**
     * Subscribe to this [LiveData] in order to get notified about inter-module navigation
     * events.
     */
    val navEvents: LiveData<InterModuleNavigationEvent> = _navEvents


    fun navigateToUserAccount() {
        _navEvents.value = of(Destination.MPAccount)
    }

    fun navigateToMovieDetails(movieId: String, movieImageUrl: String, movieTitle: String, transitionView: View) {
        _navEvents.value = of(Destination.MPMovieDetails(movieId, movieImageUrl, movieTitle, transitionView))
    }

    fun destinationReached(destinationTitle: String) {
        _navEvents.value = of(Destination.DestinationReached(destinationTitle))
    }

    fun toPrevious() {
        _navEvents.value = of(Destination.PreviousDestination)
    }

    fun performInnerNavigation(directions: NavDirections) {
        _navEvents.value = of(Destination.InnerDestination(directions))
    }
}