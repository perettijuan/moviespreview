package com.jpp.mp.screens.main.details

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.jpp.mp.R
import com.jpp.mp.assertions.assertClickable
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertNotDisplayed
import com.jpp.mp.assertions.withDrawable
import com.jpp.mp.extras.navigateToMovieDetails
import com.jpp.mp.stubbers.stubConfigurationDefault
import com.jpp.mp.stubbers.stubMovieDetails
import com.jpp.mp.stubbers.stubMovieStateFlavor1
import com.jpp.mp.stubbers.stubMovieStateFlavor2
import com.jpp.mp.stubbers.stubMovieStateFlavor3
import com.jpp.mp.stubbers.stubMovieStateFlavor4
import com.jpp.mp.stubbers.stubNowPlayingFirstPage
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Contains all the tests that exercises the actions that the user can take in the movie details.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MovieDetailsActionsUserLoggedIntegrationTest : BaseMovieDetailsIntegrationTest() {

    /*
     * favorite -> false,
     * rated -> false,
     * watchlist -> false
     */
    @Test
    fun shouldShowMovieState_flavor1() {
        //-- we need results to navigate to details screen
        stubConfigurationDefault()
        stubNowPlayingFirstPage()
        stubMovieDetails()
        stubMovieStateFlavor1()

        //-- launch app
        activityTestRule.launch()
        waitForMoviesLoadingDone()

        //-- navigate to movie details
        navigateToMovieDetails()
        waitForMovieActionsLoaded()

        //-- general state
        onActionsLoadingView().assertNotDisplayed()
        onActionsReloadButton().assertNotDisplayed()
        onActionsButton().assertDisplayed()

        //-- favorite button
        onActionsFavoriteButton().assertDisplayed()
        onActionsFavoriteButton().assertClickable()
        onActionsFavoriteButton().check(matches(withDrawable(R.drawable.ic_favorite_empty)))

        //-- rate button
        onActionsRateButton().assertDisplayed()
        onActionsRateButton().assertClickable()
        onActionsRateButton().check(matches(withDrawable(R.drawable.ic_rate_empty)))

        //-- watchlist button
        onActionsWatchlistButton().assertDisplayed()
        onActionsWatchlistButton().assertClickable()
        onActionsWatchlistButton().check(matches(withDrawable(R.drawable.ic_watchlist_empty)))
    }

    /*
     * favorite -> true,
     * rated -> false,
     * watchlist -> false
     */
    @Test
    fun shouldShowMovieState_flavor2() {
        //-- we need results to navigate to details screen
        stubConfigurationDefault()
        stubNowPlayingFirstPage()
        stubMovieDetails()
        stubMovieStateFlavor2()

        //-- launch app
        activityTestRule.launch()
        waitForMoviesLoadingDone()

        //-- navigate to movie details
        navigateToMovieDetails()
        waitForMovieActionsLoaded()

        //-- general state
        onActionsLoadingView().assertNotDisplayed()
        onActionsReloadButton().assertNotDisplayed()
        onActionsButton().assertDisplayed()

        //-- favorite button
        onActionsFavoriteButton().assertDisplayed()
        onActionsFavoriteButton().assertClickable()
        onActionsFavoriteButton().check(matches(withDrawable(R.drawable.ic_favorite_filled)))

        //-- rate button
        onActionsRateButton().assertDisplayed()
        onActionsRateButton().assertClickable()
        onActionsRateButton().check(matches(withDrawable(R.drawable.ic_rate_empty)))

        //-- watchlist button
        onActionsWatchlistButton().assertDisplayed()
        onActionsWatchlistButton().assertClickable()
        onActionsWatchlistButton().check(matches(withDrawable(R.drawable.ic_watchlist_empty)))
    }

    /*
     * favorite -> true,
     * rated -> true,
     * watchlist -> false
     */
    @Test
    fun shouldShowMovieState_flavor3() {
        //-- we need results to navigate to details screen
        stubConfigurationDefault()
        stubNowPlayingFirstPage()
        stubMovieDetails()
        stubMovieStateFlavor3()

        //-- launch app
        activityTestRule.launch()
        waitForMoviesLoadingDone()

        //-- navigate to movie details
        navigateToMovieDetails()
        waitForMovieActionsLoaded()

        //-- general state
        onActionsLoadingView().assertNotDisplayed()
        onActionsReloadButton().assertNotDisplayed()
        onActionsButton().assertDisplayed()

        //-- favorite button
        onActionsFavoriteButton().assertDisplayed()
        onActionsFavoriteButton().assertClickable()
        onActionsFavoriteButton().check(matches(withDrawable(R.drawable.ic_favorite_filled)))

        //-- rate button
        onActionsRateButton().assertDisplayed()
        onActionsRateButton().assertClickable()
        onActionsRateButton().check(matches(withDrawable(R.drawable.ic_rate_filled)))

        //-- watchlist button
        onActionsWatchlistButton().assertDisplayed()
        onActionsWatchlistButton().assertClickable()
        onActionsWatchlistButton().check(matches(withDrawable(R.drawable.ic_watchlist_empty)))
    }

    /*
     * favorite -> true,
     * rated -> true,
     * watchlist -> true
     */
    @Test
    fun shouldShowMovieState_flavor4() {
        //-- we need results to navigate to details screen
        stubConfigurationDefault()
        stubNowPlayingFirstPage()
        stubMovieDetails()
        stubMovieStateFlavor4()

        //-- launch app
        activityTestRule.launch()
        waitForMoviesLoadingDone()

        //-- navigate to movie details
        navigateToMovieDetails()
        waitForMovieActionsLoaded()

        //-- general state
        onActionsLoadingView().assertNotDisplayed()
        onActionsReloadButton().assertNotDisplayed()
        onActionsButton().assertDisplayed()

        //-- favorite button
        onActionsFavoriteButton().assertDisplayed()
        onActionsFavoriteButton().assertClickable()
        onActionsFavoriteButton().check(matches(withDrawable(R.drawable.ic_favorite_filled)))

        //-- rate button
        onActionsRateButton().assertDisplayed()
        onActionsRateButton().assertClickable()
        onActionsRateButton().check(matches(withDrawable(R.drawable.ic_rate_filled)))

        //-- watchlist button
        onActionsWatchlistButton().assertDisplayed()
        onActionsWatchlistButton().assertClickable()
        onActionsWatchlistButton().check(matches(withDrawable(R.drawable.ic_watchlist_filled)))
    }

    private fun onActionsLoadingView() = onView(withId(R.id.movieDetailActionsLoadingView))
    private fun onActionsButton() = onView(withId(R.id.movieDetailActionFab))
    private fun onActionsFavoriteButton() = onView(withId(R.id.movieDetailFavoritesFab))
    private fun onActionsWatchlistButton() = onView(withId(R.id.movieDetailWatchlistFab))
    private fun onActionsRateButton() = onView(withId(R.id.movieDetailRateFab))
    private fun onActionsReloadButton() = onView(withId(R.id.movieDetailReloadActionFab))
}