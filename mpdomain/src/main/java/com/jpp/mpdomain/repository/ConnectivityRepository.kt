package com.jpp.mpdomain.repository

import com.jpp.mpdomain.Connectivity

/**
 * Repository definition to retrieve the current [Connectivity] of the application.
 */
interface ConnectivityRepository {

    /**
     * @return the current [Connectivity] of the application.
     */
    fun getCurrentConnectivity(): Connectivity
}