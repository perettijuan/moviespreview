package com.jpp.mpdomain.handlers

import android.content.Context

interface ConnectivityHandler {
    fun isConnectedToNetwork(): Boolean


    class ConnectivityHandlerImpl(private val context: Context) : ConnectivityHandler {
        override fun isConnectedToNetwork(): Boolean {
            return with(context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager) {
                activeNetworkInfo?.isConnected ?: false
            }
        }
    }
}