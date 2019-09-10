package com.jpp.mp.screens.main

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import com.jpp.mp.R
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpcredits.NavigationCredits
import com.jpp.mpmoviedetails.NavigationMovieDetails
import com.jpp.mpperson.NavigationPerson

/**
 * Represents the view state that the MainActivity can show at any given time.
 */
data class MainActivityViewState(
        val sectionTitle: String,
        val menuBarEnabled: Boolean,
        val searchEnabled: Boolean
)

/**
 * Represents a navigation event that needs to be processed by the navigation architecture
 * components.
 */
sealed class ModuleNavigationEvent {

    /**
     * Represents an event to navigate to the previous position in the navigation tree.
     */
    object NavigateToPrevious : ModuleNavigationEvent()

    /**
     * Represents an event to navigate to a particular node in the navigation tree.
     * [directions] contains the data needed to perform the navigation.
     */
    data class NavigateToNodeWithDirections(val directions: NavDirections) : ModuleNavigationEvent()

    /**
     * Represents an event to navigate to a node in the navigation tree, where
     * [nodeId] represents the the node.
     */
    data class NavigateToNodeWithId(@IdRes val nodeId: Int) : ModuleNavigationEvent() {
        companion object {
            fun toUserAccount() = NavigateToNodeWithId(R.id.user_account_nav)
        }
    }

    /**
     * Represents an event to navigate to a node in the navigation tree attaching a [Bundle]
     * with extra data. [nodeId] represents the node to navigate to and [extras] represents
     * the extra data.
     */
    data class NavigateToNodeWithExtras(@IdRes val nodeId: Int, val extras: Bundle) : ModuleNavigationEvent() {


        companion object {
            fun toMovieDetails(destination: Destination.MPMovieDetails) = NavigateToNodeWithExtras(R.id.movie_details_nav,
                    NavigationMovieDetails.navArgs(
                            destination.movieId,
                            destination.movieImageUrl,
                            destination.movieTitle
                    )
            )

            fun toPerson(destination: Destination.MPPerson) = NavigateToNodeWithExtras(R.id.person_nav,
                    NavigationPerson.navArgs(
                            destination.personId,
                            destination.personImageUrl,
                            destination.personName
                    )
            )

            fun toCredits(destination: Destination.MPCredits) = NavigateToNodeWithExtras(R.id.credits_nav,
                    NavigationCredits.navArgs(
                            destination.movieId,
                            destination.movieTitle
                    )
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as NavigateToNodeWithExtras

            if (nodeId != other.nodeId) return false

            return true
        }

        override fun hashCode(): Int {
            return nodeId
        }
    }


}