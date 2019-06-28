package com.jpp.mp.common.navigation

import android.view.View
import java.util.concurrent.atomic.AtomicBoolean

/*
 * Contains the model entities used to perform inter-module navigation.
 */

/**
 * Represents an inter module navigation event.
 */
class InterModuleNavigationEvent(private val destination: Destination) {

    private var neverHandled = AtomicBoolean(true)

    /**
     * Execute the [action] provided if the view state wrapped has
     * not being processed yet.
     */
    fun actionIfNotHandled(action: (Destination) -> Unit) {
        if (neverHandled.compareAndSet(true, false)) {
            action(destination)
        }
    }

    companion object {
        fun of(destination: Destination) : InterModuleNavigationEvent = InterModuleNavigationEvent(destination)
    }
}


sealed class Destination {
    /*
     * The destination used to perform inter-module navigation to the
     * user account feature module.
     */
    object MPAccount : Destination()

    data class MovieDetails(val movieId: String,
                            val movieImageUrl: String,
                            val movieTitle: String,
                            val transitionView: View) : Destination()

    /*
     * Represents a Destination that is internal to a module. It is used to
     * update the Toolbar title.
     */
    data class DestinationReached(val destinationTitle: String) : Destination()
}
