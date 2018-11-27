package com.jpp.moviespreview.domainlayer

import android.content.Context

interface ConnectivityVerifier {
    fun isConnectedToNetwork(): Boolean
}


class ConnectivityVerifierImpl(private val context: Context) : ConnectivityVerifier {
    override fun isConnectedToNetwork() =
        with(context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager) {
            activeNetworkInfo?.isConnected ?: false
        }
}