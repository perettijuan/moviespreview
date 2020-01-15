package com.jpp.mp.stubbers

import androidx.test.platform.app.InstrumentationRegistry
import java.io.IOException

/**
 * Reads a file stored in the assets folder and parses it to a String object.
 */
fun readAssetFrom(assetPath: String): String {
    try {
        return InstrumentationRegistry
                .getInstrumentation().context.assets
                .open("body_files/$assetPath")
                .bufferedReader()
                .use { cl -> cl.readText() }
    } catch (e: IOException) {
        e.printStackTrace()
        throw RuntimeException(e)
    }
}
