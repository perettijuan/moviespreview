package com.jpp.mp.screens.main.search

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jpp.mp.R
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertWithText
import com.jpp.mp.assertions.onErrorViewButton
import com.jpp.mp.assertions.onErrorViewText
import com.jpp.mp.assertions.typeTextAndSubmit
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Contains the test case when the device is not connected to network and a search is executed.
 */
@RunWith(AndroidJUnit4::class)
class SearchErrorNoConnectivity : BaseSearchIntegrationTest() {

    @Test
    fun shouldShowConnectivityError() {
        activityTestRule.launchNotConnectedTonNetwork()

        openSearchScreen()

        onSearchView().perform(typeTextAndSubmit("Ramb"))

        waitForSearchStarted()

        waitForSearchDone()
        onErrorSearchView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_no_network_connection_message)
        onErrorViewButton().assertWithText(R.string.error_retry)
    }
}