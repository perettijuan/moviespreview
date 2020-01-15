package com.jpp.mp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Custom [AndroidJUnitRunner] implementation used to inject dependencies for specific test cases using
 * [MPTestApp] instead of [MPApp].
 * This runner is replaced by the built-in runner in the application's Gradle file.
 */
class MPJUnitRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, MPTestApp::class.java.name, context)
    }
}
