package com.jpp.mp.screens.main.search

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jpp.mp.MPMainActivityTestRule
import com.jpp.mp.R
import org.junit.Rule

/**
 * Base class to execute the search integration tests.
 * Motivation: initially, all the search tests were grouped into a single test class.
 * For some reason (that I don't have time to explore deeply), these grouped tests started to
 * show some flakiness - i.e.: a string resource was failing to be bound by the DataBinding library).
 * This is the best solution I've been able to find with the time I have to explore the issue.
 */
abstract class BaseSearchIntegrationTest {

    @get:Rule
    var activityTestRule = MPMainActivityTestRule()

    @get:Rule
    var wireMockRule = WireMockRule(wireMockConfig().port(8080))

    /**
     * Waits for the condition in which the loading view is visible to assume that the
     * search process has been started.
     */
    protected fun waitForSearchStarted() {
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
    protected fun waitForSearchDone() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for loading done"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<View>(R.id.searchLoadingView).visibility == View.INVISIBLE
            }
        })
    }

    protected fun openSearchScreen(): ViewInteraction = onView(withId(R.id.search_menu)).perform(click())

    protected fun onResultsRecyclerView(): ViewInteraction = onView(withId(R.id.searchResultRv))
    protected fun onSearchPlaceHolderView(): ViewInteraction = onView(withId(R.id.searchPlaceHolderIv))
    protected fun onEmptySearchView(): ViewInteraction = onView(withId(R.id.emptySearch))
    protected fun onErrorSearchView(): ViewInteraction = onView(withId(R.id.searchErrorView))
    protected fun onLoadingSearchView(): ViewInteraction = onView(withId(R.id.searchLoadingView))
}
