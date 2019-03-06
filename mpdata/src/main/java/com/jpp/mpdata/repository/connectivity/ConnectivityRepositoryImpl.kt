package com.jpp.mpdata.repository.connectivity

import android.content.Context
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.ConnectivityRepository

class ConnectivityRepositoryImpl(private val context: Context) : ConnectivityRepository {

    override fun getCurrentConnectivity(): Connectivity {
        return when (isConnectedToNetwork()) {
            true -> Connectivity.Connected
            false -> Connectivity.Disconnected
        }
    }

    private fun isConnectedToNetwork(): Boolean {
        return with(context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager) {
            activeNetworkInfo?.isConnected ?: false
        }
    }
}