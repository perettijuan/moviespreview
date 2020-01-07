package com.jpp.mp.screens.main.search

import androidx.test.espresso.action.ViewActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertItemCount
import com.jpp.mp.assertions.assertNotDisplayed
import com.jpp.mp.assertions.onErrorViewButton
import com.jpp.mp.assertions.typeTextAndSubmit
import com.jpp.mp.stubbers.stubConfigurationDefault
import com.jpp.mp.stubbers.stubSearchDefault
import com.jpp.mp.stubbers.stubSearchWithError
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SearchErrorRetry : BaseSearchIntegrationTest() {

    @Test
    fun shouldExecuteSearchWhenRetryAfterAnError() {
        stubSearchWithError()

        activityTestRule.launch()

        openSearchScreen()

        onSearchView().perform(typeTextAndSubmit("Ramb"))

        waitForSearchStarted()

        waitForSearchDone()
        onErrorSearchView().assertDisplayed()

        //-- stub a possible result
        stubConfigurationDefault()
        stubSearchDefault()
        onErrorViewButton().perform(ViewActions.click())

        waitForSearchStarted()
        waitForSearchDone()
        onLoadingSearchView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onSearchPlaceHolderView().assertNotDisplayed()
        onResultsRecyclerView().assertDisplayed()
        onResultsRecyclerView().assertItemCount(19)
    }
}