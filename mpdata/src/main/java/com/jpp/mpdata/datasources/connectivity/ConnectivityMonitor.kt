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
//TODO JPP delete ME
interface ConnectivityMonitor {
    /**
     * Call this method when the monitor needs to start monitoring the network changes.
     * It will notify all listeners added in [addListener] of connectivity change events.
     */
    fun startMonitoring()

    /**
     * Call this method when the monitor should stop monitoring network changes.
     */
    fun stopMonitoring()

    /**
     * Ads a new [connectivityListener] to be notified when a connectivity event happens.
     */
    fun addListener(connectivityListener: () -> Unit)


    @RequiresApi(Build.VERSION_CODES.N)
    class ConnectivityMonitorAPI24(context: Context) : ConnectivityMonitor {
        private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        private val listeners = mutableListOf<() -> Unit>()

        private val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network?) {
                listeners.forEach { it.invoke() }
            }

            override fun onAvailable(network: Network?) {
                listeners.forEach { it.invoke() }
            }
        }

        override fun startMonitoring() {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        }

        override fun stopMonitoring() {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            listeners.clear()
        }

        override fun addListener(connectivityListener: () -> Unit) {
            listeners.add(connectivityListener)
        }
    }

    class ConnectivityMonitorAPI23(private val context: Context) : ConnectivityMonitor {

        private val listeners = mutableListOf<() -> Unit>()

        private val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                listeners.forEach { it.invoke() }
            }
        }

        override fun startMonitoring() {
            context.registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }

        override fun stopMonitoring() {
            context.unregisterReceiver(receiver)
            listeners.clear()
        }

        override fun addListener(connectivityListener: () -> Unit) {
            listeners.add(connectivityListener)
        }
    }
}