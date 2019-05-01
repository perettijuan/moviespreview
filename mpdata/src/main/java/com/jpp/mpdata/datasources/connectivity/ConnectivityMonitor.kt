package com.jpp.mpdata.datasources.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Monitors connectivity changes - when requested to monitor - and notifies to the interested
 * client when an event is triggered.
 */
interface ConnectivityMonitor {
    /**
     * Call this method when the monitor needs to start monitoring the network changes.
     * It will notify [connectivityListener] of connectivity change events.
     */
    fun startMonitoring(connectivityListener: () -> Unit)

    /**
     * Call this method when the monitor should stop monitoring network changes.
     */
    fun stopMonitoring()


    @RequiresApi(Build.VERSION_CODES.N)
    class ConnectivityMonitorAPI24(context: Context) : ConnectivityMonitor {

        private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        private lateinit var listener: () -> Unit

        private val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network?) {
                listener()
            }

            override fun onAvailable(network: Network?) {
                listener()
            }
        }

        override fun startMonitoring(connectivityListener: () -> Unit) {
            listener = connectivityListener
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        }

        override fun stopMonitoring() {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    class ConnectivityMonitorAPI23(private val context: Context) : ConnectivityMonitor {

        private lateinit var listener: () -> Unit

        private val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                listener()
            }
        }

        override fun startMonitoring(connectivityListener: () -> Unit) {
            listener = connectivityListener
            context.registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }

        override fun stopMonitoring() {
            context.unregisterReceiver(receiver)
        }
    }
}