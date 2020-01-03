package com.jpp.mp

import android.net.ConnectivityManager
import android.net.NetworkInfo
import io.mockk.every
import io.mockk.mockk

/**
 * [MPApp] implementation used to inject dependencies for specific use cases.
 *
 * Connectivity dependency => in order to test no-connectivity scenarios, this class provides
 * [isConnectedToNetwork] property that can be changed. This value will be passed to the [ConnectivityManager]
 * in order to provide the connectivity status.
 */
class MPTestApp : MPApp() {

    private val localConnectivityManager = mockk<ConnectivityManager>()
    private val localActiveNetwork = mockk<NetworkInfo>(relaxed = true)
    var isConnectedToNetwork = true


    override fun getSystemService(name: String): Any {
        if (name == CONNECTIVITY_SERVICE) {
            every { localConnectivityManager.activeNetworkInfo } returns localActiveNetwork
            every { localActiveNetwork.isConnected } returns isConnectedToNetwork
            return localConnectivityManager
        }
        return super.getSystemService(name)
    }
}