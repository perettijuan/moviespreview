package com.jpp.mp.common.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jpp.mp.common.livedata.HandledEvent
import com.jpp.mp.common.livedata.HandledEvent.Companion.of
import com.jpp.mp.common.navigation.Destination

/**
 * Navigation support:
 * The application uses the Android Navigation Architecture Components to perform the navigation between
 * different screens of the application. This provides a good number of benefits - all listed in
 * the Navigation Architecture web page - but it also comes with some difficulties.
 *
 * Updating the ActionBar title when a new section of the application is shown to the user is
 * one of the problems.
 * This ViewModel is part of the solution implemented to such problem: It provides a [LiveData]
 * with updates of [Destination]. Each time the VMs detects that the new section is being shown and
 * the UI is ready to render, the VM produces a new event that will be captured in order to update
 * the screen title.
 *
 * Another problem is related to how to navigate from a point to another when those points are in
 * different modules. In these cases, the origin does not knows anything about the new destination.
 * To such cases, this VM provides a mechanism to request a navigation to a new destination
 * each time it's needed.
 *
 * To see the full implementation of this solution, check [MPFragment] definition.
 */
abstract class MPViewModel : ViewModel() {

    private val _destinationsEvent = MutableLiveData<Destination>()
    val destinationEvents: LiveData<Destination> get() = _destinationsEvent

    private val _navigationEvent = MutableLiveData<HandledEvent<Destination>>()
    val navigationEvents: LiveData<HandledEvent<Destination>> get() = _navigationEvent

    /**
     * Called when a [Destination] has been reached in the application. A call
     * to this method will produce a new event in [destinationEvents].
     */
    protected fun updateCurrentDestination(destination: Destination) {
        _destinationsEvent.value = destination
    }

    /**
     * Called when a new [destination] must be reached. This will post
     * a new event to [navigationEvents] in order to request a navigation
     * to the provided [destination].
     */
    protected fun navigateTo(destination: Destination) {
        _navigationEvent.value = of(destination)
    }
}
