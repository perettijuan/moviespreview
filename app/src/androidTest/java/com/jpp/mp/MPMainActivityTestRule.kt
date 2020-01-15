package com.jpp.mp

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.jpp.mp.main.MainActivity

/**
 * [ActivityTestRule] for the [MainActivity] that allows the injection of specific scenarios.
 */
class MPMainActivityTestRule : ActivityTestRule<MainActivity>(MainActivity::class.java,
        true,
        false) {

    // Used to mock the connectivity state.
    private var isConnectedToNetwork = true

    // When != null, means the scenario is assuming user logged in.
    private var sessionId: String? = null

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MPTestApp
        app.isConnectedToNetwork = isConnectedToNetwork

        with(InstrumentationRegistry.getInstrumentation().targetContext) {
            // IMPORTANT: clear all the data stored in the application's database in order to run a clean test.
            deleteDatabase("MPRoomDataBase")

            // IMPORTANT: when sessionId is not null, we assume the user logged in
            // IMPORTANT2: the file name and the preference name is based on [SessionDbImpl]
            val pref = getSharedPreferences("com.jpp.moviespreview.preferences.session", Context.MODE_PRIVATE)
            with(pref.edit()) {
                putString("session_stored", sessionId)
            }
        }
    }

    /**
     * Launches the test in a normal scenario.
     */
    fun launch() {
        launchActivity(android.content.Intent())
    }

    /**
     * Launches the test in a not connected to network scenario.
     */
    fun launchNotConnectedTonNetwork() {
        isConnectedToNetwork = false
        launchActivity(android.content.Intent())
    }

    /**
     * Launches the test with the user logged in state.
     */
    fun launchWithUserLoggedIn() {
        sessionId = sessionIdToUse
        launchActivity(android.content.Intent())
    }

    /**
     * Simulates an internet disconnection
     */
    fun simulateNotConnectedToNetwork() {
        isConnectedToNetwork = false
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MPTestApp
        app.isConnectedToNetwork = isConnectedToNetwork
    }

    private companion object {
        const val sessionIdToUse = "57fd1c2430aecf45c9e9f7283042843c86921fee"
    }
}
