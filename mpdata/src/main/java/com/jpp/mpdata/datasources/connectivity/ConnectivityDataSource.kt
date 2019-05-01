package com.jpp.mpdata.datasources.connectivity

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.Connectivity

/**
 * Datasource definition to detect the connectivity changes and notify the upper layers when
 * a connectivity change is detected.
 *
 * LIFECYCLE:
 *  - should be enabled when the container activity is started.
 *  - should be disabled when the container activity is stopped.
 */
interface ConnectivityDataSource {
    /**
     * Call this method in order to enable connectivity updates posted in [connectivityUpdates].
     */
    fun enableUpdates()

    /**
     * Call this method to disable connectivity updates posted in [connectivityUpdates].
     */
    fun disableUpdates()

    /**
     * Subscribe to this LiveData object in order to get notifications about [Connectivity] changes.
     */
    fun connectivityUpdates(): LiveData<Connectivity>


    class Impl(private val monitor: ConnectivityMonitor,
               private val context: Context) : ConnectivityDataSource {

        private val updates = MutableLiveData<Connectivity>()

        override fun enableUpdates() {
            monitor.startMonitoring { updates.postValue(getCurrentConnectivity()) }
        }

        override fun disableUpdates() {
            monitor.stopMonitoring()
        }

        override fun connectivityUpdates(): LiveData<Connectivity> = updates


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

}