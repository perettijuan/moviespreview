package com.jpp.mp.common.navigation

import android.view.View
import androidx.navigation.NavDirections
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
        fun of(destination: Destination): InterModuleNavigationEvent = InterModuleNavigationEvent(destination)
    }
}


sealed class Destination {
    /*
     * Represents a request to perform an up destination.
     */
    object PreviousDestination : Destination()

    /*
     * The destination used to perform inter-module navigation to the
     * user account feature module.
     */
    object MPAccount : Destination()

    /*
     * Destination used to perform inter-module navigation to the details
     * module.
     */
    data class MPMovieDetails(val movieId: String,
                              val movieImageUrl: String,
                              val movieTitle: String,
                              val transitionView: View) : Destination()

    /*
     * Destination used to perform inter-module navigation to the persons
     * module.
     */
    data class MPPerson(val personId: String,
                        val personImageUrl: String,
                        val personName: String) : Destination()

    /*
     * Destination used to perform inter-module navigation to the credits
     * module.
     */
    data class MPCredits(val movieId: Double,
                         val movieTitle: String) : Destination()

    /*
     * Represents a Destination that is internal to a module.
     */
    data class InnerDestination(val directions: NavDirections) : Destination()

    /*
     * Represents the search feature of the application.
     */
    object MPSearch : Destination()

    /*
     * Represents a Destination that is reached. It is used to
     * update the Toolbar title.
     */
    data class ReachedDestination(val destinationTitle: String) : Destination()
}
