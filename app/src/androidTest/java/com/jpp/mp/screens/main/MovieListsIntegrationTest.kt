package com.jpp.mp.screens.main

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jpp.mp.R
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertItemCount
import com.jpp.mp.assertions.assertNotDisplayed
import com.jpp.mp.assertions.withViewInRecyclerView
import com.jpp.mp.extras.launch
import com.jpp.mp.main.MainActivity
import com.jpp.mp.stubbers.stubNowPlayingFirstPage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieListsIntegrationTest {

    @get:Rule
    var activityTestRule = ActivityTestRule<MainActivity>(
            MainActivity::class.java,
            true,
            false)

    @get:Rule
    var wireMockRule = WireMockRule(wireMockConfig().port(8080))

    @Test
    fun shouldNowPlayingFirstPlay() {
        stubNowPlayingFirstPage()

        activityTestRule.launch()

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
    fun shouldShowConnectivityError() {

    }


    private fun waitForDoneLoading() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for items in list"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<View>(R.id.moviesLoadingView).visibility == View.INVISIBLE
            }
        })
    }


    private fun onMoviesList() = onView(withId(R.id.movieList))
    private fun onMoviesLoadingView() = onView(withId(R.id.moviesLoadingView))
    private fun onMoviesErrorView() = onView(withId(R.id.movieListErrorView))
}