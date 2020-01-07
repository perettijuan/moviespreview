package com.jpp.mp

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

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MPTestApp
        app.isConnectedToNetwork = isConnectedToNetwork

        /*
         * IMPORTANT: clear all the data stored in the application's database in order to run a clean test.
         */
        InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase("MPRoomDataBase")
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
     * Simulates an internet disconnection
     */
    fun simulateNotConnectedToNetwork() {
        isConnectedToNetwork = false
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MPTestApp
        app.isConnectedToNetwork = isConnectedToNetwork
    }
}