package com.jpp.mp.screens.main.movies

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jpp.mp.MPMainActivityTestRule
import com.jpp.mp.R
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertItemCount
import com.jpp.mp.assertions.assertNotDisplayed
import com.jpp.mp.assertions.assertWithText
import com.jpp.mp.assertions.onErrorViewButton
import com.jpp.mp.assertions.onErrorViewText
import com.jpp.mp.assertions.withViewInRecyclerView
import com.jpp.mp.stubbers.stubConfigurationDefault
import com.jpp.mp.stubbers.stubNowPlayingFirstPage
import com.jpp.mp.stubbers.stubNowPlayingWithError
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class NowPlayingMovieListIntegrationTests {

    @get:Rule
    var activityTestRule = MPMainActivityTestRule()

    @get:Rule
    var wireMockRule = WireMockRule(wireMockConfig().port(8080))

    @Test
    fun shouldShowFirstMoviesPage() {
        stubConfigurationDefault()
        stubNowPlayingFirstPage()

        activityTestRule.launch()

        onMoviesLoadingView().assertDisplayed()

        waitForDoneLoading()

        onMoviesLoadingView().assertNotDisplayed()
        onMoviesErrorView().assertNotDisplayed()
        onMoviesList().assertDisplayed()
        onMoviesList().assertItemCount(20)

        // Verifying a couple of items should be enough

        // Item 0
        onView(withViewInRecyclerView(R.id.movieList, 0, R.id.movieItemTitle))
                .check(matches(withText("Ad Astra")))

        onView(withViewInRecyclerView(R.id.movieList, 0, R.id.movieItemPopularityText))
                .check(matches(withText("498.648")))

        onView(withViewInRecyclerView(R.id.movieList, 0, R.id.movieItemVoteCountText))
                .check(matches(withText("1605.0")))

        // Item 1
        onView(withViewInRecyclerView(R.id.movieList, 1, R.id.movieItemTitle))
                .check(matches(withText("Star Wars: The Rise of Skywalker")))

        onView(withViewInRecyclerView(R.id.movieList, 1, R.id.movieItemPopularityText))
                .check(matches(withText("400.039")))

        onView(withViewInRecyclerView(R.id.movieList, 1, R.id.movieItemVoteCountText))
                .check(matches(withText("1767.0")))
    }

    @Test
    fun shouldRequestMorePagesWhenUserScrolls() {
        stubConfigurationDefault()
        stubNowPlayingFirstPage()

        activityTestRule.launch()

        onMoviesLoadingView().assertDisplayed()

        waitForDoneLoading()

        onMoviesLoadingView().assertNotDisplayed()
        onMoviesErrorView().assertNotDisplayed()
        onMoviesList().assertDisplayed()
        onMoviesList().assertItemCount(20)

        // perform the scrolling
        onMoviesList().perform(scrollToPosition<RecyclerView.ViewHolder>(19))

        waitForMoreMovies()

        onMoviesList().assertItemCount(40)
    }

    @Test
    fun shouldShowConnectivityError() {
        activityTestRule.launchNotConnectedTonNetwork()

        waitForDoneLoading()

        onMoviesLoadingView().assertNotDisplayed()
        onMoviesList().assertNotDisplayed()

        onMoviesErrorView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_no_network_connection_message)
        onErrorViewButton().assertWithText(R.string.error_retry)
    }

    @Test
    fun shouldShowUnknownError() {
        stubNowPlayingWithError()

        activityTestRule.launch()

        waitForDoneLoading()

        onMoviesLoadingView().assertNotDisplayed()
        onMoviesList().assertNotDisplayed()

        onMoviesErrorView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_unexpected_error_message)
        onErrorViewButton().assertWithText(R.string.error_retry)
    }

    private fun waitForDoneLoading() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for loading done"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<View>(R.id.moviesLoadingView).visibility == View.INVISIBLE
            }
        })
    }

    private fun waitForMoreMovies() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for loading done"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<RecyclerView>(R.id.movieList).adapter?.let { it.itemCount > 20 }
                        ?: false
            }
        })
    }

    private fun onMoviesList() = onView(withId(R.id.movieList))
    private fun onMoviesLoadingView() = onView(withId(R.id.moviesLoadingView))
    private fun onMoviesErrorView() = onView(withId(R.id.movieListErrorView))
}
