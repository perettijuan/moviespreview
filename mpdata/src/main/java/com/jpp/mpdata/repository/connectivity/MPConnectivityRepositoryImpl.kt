package com.jpp.mpdata.repository.connectivity

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdata.datasources.connectivity.ConnectivityMonitor
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.MPConnectivityRepository

/**
 * [MPConnectivityRepository] implementation. Notifies every time a new [Connectivity] becomes
 * available.
 */
class MPConnectivityRepositoryImpl(monitor: ConnectivityMonitor,
                                   private val context: Context) : MPConnectivityRepository {
    private val updates by lazy { MutableLiveData<Connectivity>() }

    init {
        monitor.addListener { getCurrentConnectivity().let { updates.postValue(it) } }
    }

    override fun data(): LiveData<Connectivity> = updates

    override fun getCurrentConnectivity(receptor: (Connectivity) -> Unit) {
        receptor(getCurrentConnectivity())
    }

    private fun getCurrentConnectivity(): Connectivity {
        return when (isConnectedToNetwork()) {
            true -> Connectivity.Connected
            false -> Connectivity.Disconnected
        }
    }

    private fun isConnectedToNetwork(): Boolean {
        return with(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) {
            activeNetworkInfo?.isConnected ?: false
        }
    }
}
