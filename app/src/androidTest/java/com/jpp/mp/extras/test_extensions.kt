package com.jpp.mp.extras

import android.app.Activity
import androidx.test.rule.ActivityTestRule

/**
 * Extension function to launch the Activity under test.
 */
fun <T : Activity> ActivityTestRule<T>.launch() {
    launchActivity(android.content.Intent())
}