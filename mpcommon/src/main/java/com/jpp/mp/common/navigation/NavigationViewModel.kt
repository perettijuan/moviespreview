package com.jpp.mp.common.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jpp.mp.common.navigation.InterModuleNavigationEvent.Companion.of
import javax.inject.Inject

/**
 * Provides functionality to perform inter-modules navigation.
 * The application is organized in different feature-modules and uses the Navigation Architecture
 * library from Architecture Components. The problem this ViewModel solves is how to navigate
 * from a feature-module A to a feature-module B without adding a dependency to the feature-module A.
 */
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _navEvents = MutableLiveData<InterModuleNavigationEvent>()
    val navEvents: LiveData<InterModuleNavigationEvent> = _navEvents

    private val _reachedDestinations = MutableLiveData<Destination>()
    val reachedDestinations: LiveData<Destination> = _reachedDestinations

    fun navigateTo(destination: Destination) {
        _navEvents.value = of(destination)
    }

    fun destinationReached(destination: Destination) {
        _reachedDestinations.value = destination
    }
}
