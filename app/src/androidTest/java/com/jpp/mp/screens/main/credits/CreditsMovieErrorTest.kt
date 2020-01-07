package com.jpp.mp.screens.main.credits

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jpp.mp.R
import com.jpp.mp.assertions.NestedScrollViewExtension
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertNotDisplayed
import com.jpp.mp.assertions.assertWithText
import com.jpp.mp.assertions.onErrorViewButton
import com.jpp.mp.assertions.onErrorViewText
import com.jpp.mp.extras.navigateToMovieCredits
import com.jpp.mp.extras.navigateToMovieDetails
import com.jpp.mp.stubbers.stubConfigurationDefault
import com.jpp.mp.stubbers.stubMovieCreditsWithError
import com.jpp.mp.stubbers.stubMovieDetails
import com.jpp.mp.stubbers.stubNowPlayingFirstPage
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreditsMovieErrorTest : BaseCreditsIntegrationTest() {
    @Test
    fun shouldShowConnectivityError() {
        stubConfigurationDefault()
        stubNowPlayingFirstPage()
        stubMovieDetails()

        //-- launch app and move to movie details
        activityTestRule.launch()
        waitForMoviesLoadingDone()
        navigateToMovieDetails()
        waitForMovieDetailsLoaded()

        activityTestRule.simulateNotConnectedToNetwork()

        //-- go to movie credits
        onView(withId(R.id.detailCreditsSelectionView)).perform(NestedScrollViewExtension())
        navigateToMovieCredits()
        waitForMovieCreditsLoaded()

        onCreditsLoadingView().assertNotDisplayed()
        onCreditsListView().assertNotDisplayed()

        onCreditsErrorView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_no_network_connection_message)
        onErrorViewButton().assertWithText(R.string.error_retry)
    }


    @Test
    fun shouldShowUnknownError() {
        stubConfigurationDefault()
        stubNowPlayingFirstPage()
        stubMovieDetails()
        stubMovieCreditsWithError()

        //-- launch app and move to movie details
        activityTestRule.launch()
        waitForMoviesLoadingDone()
        navigateToMovieDetails()
        waitForMovieDetailsLoaded()

        //-- go to movie credits
        onView(withId(R.id.detailCreditsSelectionView)).perform(NestedScrollViewExtension())
        navigateToMovieCredits()
        waitForMovieCreditsLoaded()

        onCreditsLoadingView().assertNotDisplayed()
        onCreditsListView().assertNotDisplayed()

        onCreditsErrorView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_unexpected_error_message)
        onErrorViewButton().assertWithText(R.string.error_retry)
    }
}