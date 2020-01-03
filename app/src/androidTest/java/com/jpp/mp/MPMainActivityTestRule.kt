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
}