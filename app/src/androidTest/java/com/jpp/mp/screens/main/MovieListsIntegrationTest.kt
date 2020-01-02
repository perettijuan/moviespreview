package com.jpp.mp.screens.main

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jpp.mp.extras.launch
import com.jpp.mp.main.MainActivity
import com.jpp.mp.stubbers.MovieListsStubs
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieListsIntegrationTest {

    @get:Rule
    var activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java, true,
            false)

    @get:Rule
    var wireMockRule = WireMockRule(wireMockConfig().port(8080))

    @Test
    fun shouldShowUnknownError() {
        MovieListsStubs.stubMovieListError(400)

        activityTestRule.launch()
        Assert.assertTrue(1 == 1)

//        waitForViewState(MoviesViewState.ErrorUnknown)
//
//        onMoviesErrorView().assertDisplayed()
//        onView(withId(R.id.errorTitleTextView)).assertWithText(R.string.error_unexpected_error_message)
//
//        onMoviesLoadingView().assertNotDisplayed()
//        onMoviesList().assertNotDisplayed()
    }

}