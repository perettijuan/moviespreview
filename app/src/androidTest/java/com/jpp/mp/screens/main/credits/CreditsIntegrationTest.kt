package com.jpp.mp.screens.main.credits

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jpp.mp.R
import com.jpp.mp.assertions.NestedScrollViewExtension
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertItemCount
import com.jpp.mp.assertions.assertNotDisplayed
import com.jpp.mp.assertions.assertWithText
import com.jpp.mp.assertions.onActionBarTitle
import com.jpp.mp.assertions.withViewInRecyclerView
import com.jpp.mp.extras.navigateToMovieCredits
import com.jpp.mp.extras.navigateToMovieDetails
import com.jpp.mp.stubbers.stubConfigurationDefault
import com.jpp.mp.stubbers.stubMovieCredits
import com.jpp.mp.stubbers.stubMovieDetails
import com.jpp.mp.stubbers.stubNowPlayingFirstPage
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreditsIntegrationTest : BaseCreditsIntegrationTest() {

    @Test
    fun shouldRenderMovieCredits() {
        // -- we need results to navigate to details screen
        stubConfigurationDefault()
        stubNowPlayingFirstPage()
        stubMovieDetails()
        stubMovieCredits()

        // -- launch app and move to movie details
        activityTestRule.launch()
        waitForMoviesLoadingDone()
        navigateToMovieDetails()
        waitForMovieDetailsLoaded()

        // -- go to movie credits
        onView(withId(R.id.detailCreditsSelectionView)).perform(NestedScrollViewExtension())
        navigateToMovieCredits()
        waitForMovieCreditsLoaded()

        // -- Action bar
        onActionBarTitle().assertWithText("Ad Astra")

        onCreditsLoadingView().assertNotDisplayed()
        onCreditsErrorView().assertNotDisplayed()

        onCreditsListView().assertDisplayed()
        onCreditsListView().assertItemCount(101)

        // Verifying a couple of items should be enough

        // Item 0
        onView(withViewInRecyclerView(R.id.creditsRv, 0, R.id.creditsItemTitle))
                .check(matches(withText("Roy McBride")))

        onView(withViewInRecyclerView(R.id.creditsRv, 0, R.id.creditsItemSubTitle))
                .check(matches(withText("Brad Pitt")))

        // Item 1
        onView(withViewInRecyclerView(R.id.creditsRv, 1, R.id.creditsItemTitle))
                .check(matches(withText("H. Clifford McBride")))

        onView(withViewInRecyclerView(R.id.creditsRv, 1, R.id.creditsItemSubTitle))
                .check(matches(withText("Tommy Lee Jones")))

        // Item 6
        onView(withViewInRecyclerView(R.id.creditsRv, 6, R.id.creditsItemTitle))
                .check(matches(withText("Chip Garnes")))

        onView(withViewInRecyclerView(R.id.creditsRv, 6, R.id.creditsItemSubTitle))
                .check(matches(withText("Greg Bryk")))
    }
}
