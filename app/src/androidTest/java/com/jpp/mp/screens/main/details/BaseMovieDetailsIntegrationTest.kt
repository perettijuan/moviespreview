package com.jpp.mp.screens.main.details

import android.view.View
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jpp.mp.MPMainActivityTestRule
import com.jpp.mp.R
import org.junit.Rule

abstract class BaseMovieDetailsIntegrationTest {

    @get:Rule
    var activityTestRule = MPMainActivityTestRule()

    @get:Rule
    var wireMockRule = WireMockRule(WireMockConfiguration.wireMockConfig().port(8080))

    /**
     * Adds a condition to wait until the movies in the home screen are loaded.
     */
    protected fun waitForMoviesLoadingDone() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for movies list done"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<View>(R.id.moviesLoadingView).visibility == View.INVISIBLE
            }
        })
    }

    /**
     * Adds a condition to wait until the details of the movie is loaded.
     */
    protected fun waitForMovieDetailsLoaded() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for movie details loading done"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<View>(R.id.movieDetailLoadingView).visibility == View.INVISIBLE
            }
        })
    }

    /**
     * Adds a condition to wait until the details of the movie is loaded.
     */
    protected fun waitForMovieActionsLoaded() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for movie details loading done"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<View>(R.id.movieDetailActionsLoadingView).visibility == View.INVISIBLE
            }
        })
    }
}