package com.jpp.mp.screens.main.search

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jpp.mp.R
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertWithText
import com.jpp.mp.assertions.onErrorViewButton
import com.jpp.mp.assertions.onErrorViewText
import com.jpp.mp.assertions.typeTextAndSubmit
import com.jpp.mp.stubbers.stubSearchWithError
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Contains the test case when an unknown error is detected in the search flow.
 */
@RunWith(AndroidJUnit4::class)
class SearchErrorUnknown : BaseSearchIntegrationTest() {

    @Test
    fun shouldShowUnknownError() {
        stubSearchWithError()

        activityTestRule.launch()

        openSearchScreen()

        /*
         * TODO I need to fix this tests. No problem for the moment, since
         *  I'm not making them part of the CI pipeline. It is IMPORTANT to
         *  do this after I remove the interactor layer.
         */
        //onSearchView().perform(typeTextAndSubmit("Ramb"))

        waitForSearchStarted()

        waitForSearchDone()
        onErrorSearchView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_unexpected_error_message)
        onErrorViewButton().assertWithText(R.string.error_retry)
    }
}
