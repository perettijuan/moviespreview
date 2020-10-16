package com.jpp.mpdata.repository.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.ConnectivityRepository

class ConnectivityRepositoryImpl(private val context: Context) : ConnectivityRepository {

    override suspend fun getCurrentConnectivity(): Connectivity {
        return when (isConnectedToNetwork()) {
            true -> Connectivity.Connected
            false -> Connectivity.Disconnected
        }
    }

    private fun isConnectedToNetwork(): Boolean {
        return if (Build.VERSION.SDK_INT >= API_LEVEL_23) {
            isConnectedToNetworkApiLevel23()
        } else {
            isConnectedToNetworkPriorApiLevel23()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnectedToNetworkApiLevel23(): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return when {
            (capabilities == null) -> false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    @Suppress("DEPRECATION")
    private fun isConnectedToNetworkPriorApiLevel23(): Boolean {
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }


    private val connectivityManager: ConnectivityManager
        get() = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private companion object {
        const val API_LEVEL_23 = Build.VERSION_CODES.M
    }
}
