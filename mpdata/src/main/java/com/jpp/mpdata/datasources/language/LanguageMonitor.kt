package com.jpp.mpdata.datasources.language

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * Monitors language changes in the device in which the application is running.
 * It will detect language changes that the user can do, either from the settings app
 * or from any other application that provides such feature.
 */
interface LanguageMonitor {

    /**
     * Call this method when the monitor needs to start monitoring the language changes.
     * It will notify all listeners added in [addListener] of language change events.
     */
    fun startMonitoring()

    /**
     * Call this method when the monitor should stop monitoring language changes.
     */
    fun stopMonitoring()

    /**
     * Ads a new [languageChange] to be notified when a language event is detected.
     */
    fun addListener(languageChange: () -> Unit)


    class Impl(private val context: Context) : LanguageMonitor {

        private val listeners = mutableListOf<() -> Unit>()
        private val languageReceiver = LanguageReceiver {
            listeners.forEach { it.invoke() }
        }

        override fun startMonitoring() {
            context.registerReceiver(languageReceiver, IntentFilter(Intent.ACTION_LOCALE_CHANGED))
        }

        override fun stopMonitoring() {
            listeners.clear()
            context.unregisterReceiver(languageReceiver)
        }

        override fun addListener(languageChange: () -> Unit) {
            listeners.add(languageChange)
        }

        private class LanguageReceiver(private val callback: () -> Unit) : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                callback()
            }
        }
    }
}