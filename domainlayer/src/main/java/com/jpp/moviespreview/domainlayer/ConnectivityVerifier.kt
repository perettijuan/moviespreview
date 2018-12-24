package com.jpp.moviespreview.domainlayer

import android.content.Context

/**
 * Has the responsibility of verifying if the device is currently
 * connected to a network.
 * It doesn't checks if there is internet access, just checks that a network
 * can be accessed.
 */
interface ConnectivityVerifier {
    fun isConnectedToNetwork(): Boolean
}


class ConnectivityVerifierImpl(private val context: Context) : ConnectivityVerifier {
    override fun isConnectedToNetwork() =
        with(context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager) {
            activeNetworkInfo?.isConnected ?: false
        }
}