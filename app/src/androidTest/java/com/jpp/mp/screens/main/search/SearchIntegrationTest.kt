package com.jpp.mp.screens.main.search

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jpp.mp.MPMainActivityTestRule
import com.jpp.mp.R
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertItemCount
import com.jpp.mp.assertions.assertNotDisplayed
import com.jpp.mp.assertions.onActionBarBackButton
import com.jpp.mp.assertions.typeText
import com.jpp.mp.assertions.withViewInRecyclerView
import com.jpp.mp.stubbers.stubConfigurationDefault
import com.jpp.mp.stubbers.stubSearchDefault
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchIntegrationTest {

    @get:Rule
    var activityTestRule = MPMainActivityTestRule()

    @get:Rule
    var wireMockRule = WireMockRule(wireMockConfig().port(8080))


    @Test
    fun shouldOpenSearchScreenWhenSearchPressed() {
        activityTestRule.launch()

        openSearchScreen()

        onResultsRecyclerView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onLoadingSearchView().assertNotDisplayed()

        onSearchView().assertDisplayed()
        onSearchPlaceHolderView().assertDisplayed()

        onActionBarBackButton().assertDisplayed()
    }

    @Test
    fun shouldSearchWhenCharacterThresholdIsReached() {
        stubConfigurationDefault()
        stubSearchDefault()

        activityTestRule.launch()

        openSearchScreen()

        onSearchView().perform(typeText("Ramb"))

        waitForSearchStarted()
        waitForSearchDone()

        onLoadingSearchView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onResultsRecyclerView().assertDisplayed()

        onResultsRecyclerView().assertItemCount(19)

        // Verifying a couple of items should be enough

        // Item 0
        onView(withViewInRecyclerView(R.id.searchResultRv, 0, R.id.searchItemTitleTxt))
                .check(ViewAssertions.matches(ViewMatchers.withText("Rambo: Last Blood")))
//TODO check image type
//        onView(withViewInRecyclerView(R.id.searchResultRv, 0, R.id.movieItemPopularityText))
//                .check(ViewAssertions.matches(ViewMatchers.withText("498.648")))

        // Item 1
        onView(withViewInRecyclerView(R.id.searchResultRv, 1, R.id.searchItemTitleTxt))
                .check(ViewAssertions.matches(ViewMatchers.withText("Rambo III")))
        // Item 2
        onView(withViewInRecyclerView(R.id.searchResultRv, 2, R.id.searchItemTitleTxt))
                .check(ViewAssertions.matches(ViewMatchers.withText("Rambo")))
    }


    /**
     * Waits for the condition in which the loading view is visible to assume that the
     * search process has been started.
     */
    private fun waitForSearchStarted() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for loading done"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<View>(R.id.searchLoadingView).visibility == View.VISIBLE
            }
        })
    }

    /**
     * Register a condition to wait for the loading view is hidden to assume that the search is
     * finished.
     */
    private fun waitForSearchDone() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for loading done"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<View>(R.id.searchLoadingView).visibility == View.INVISIBLE
            }
        })
    }

    private fun openSearchScreen() = onView(withId(R.id.search_menu)).perform(click())

    private fun onResultsRecyclerView() = onView(withId(R.id.searchResultRv))
    private fun onSearchPlaceHolderView() = onView(withId(R.id.searchPlaceHolderIv))
    private fun onEmptySearchView() = onView(withId(R.id.emptySearch))
    private fun onErrorSearchView() = onView(withId(R.id.searchErrorView))
    private fun onLoadingSearchView() = onView(withId(R.id.searchLoadingView))
    private fun onSearchView() = onView(withId(R.id.mainSearchView))
}