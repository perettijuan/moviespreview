package com.jpp.mpdomain.repository

import androidx.lifecycle.LiveData
import com.jpp.mpdomain.Connectivity

/**
 * Repository definition to track [Connectivity] states.
 * Notifies to any subscribed client when a connectivity event is detected providing the new
 * [Connectivity] state.
 */
interface MPConnectivityRepository {
    /**
     * Subscribe to this LiveData object in order to get
     * notifications about [Connectivity] changes.
     */
    fun data(): LiveData<Connectivity>

    /**
     * Retrieves the current connectivity state instantaneously. Will invoke
     * [receptor] with the current [Connectivity].
     */
    fun getCurrentConnectivity(receptor: (Connectivity) -> Unit)
}