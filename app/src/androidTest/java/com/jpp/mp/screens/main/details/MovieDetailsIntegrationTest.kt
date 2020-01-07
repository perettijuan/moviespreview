package com.jpp.mp.screens.main.details

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.jpp.mp.R
import com.jpp.mp.assertions.NestedScrollViewExtension
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertItemCount
import com.jpp.mp.assertions.assertNotDisplayed
import com.jpp.mp.assertions.assertWithText
import com.jpp.mp.assertions.onActionBarTitle
import com.jpp.mp.assertions.onErrorViewButton
import com.jpp.mp.assertions.onErrorViewText
import com.jpp.mp.assertions.withDrawable
import com.jpp.mp.assertions.withViewInRecyclerView
import com.jpp.mp.stubbers.stubConfigurationDefault
import com.jpp.mp.stubbers.stubMovieDetails
import com.jpp.mp.stubbers.stubMovieDetailsWithError
import com.jpp.mp.stubbers.stubNowPlayingFirstPage
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Contains all the tests that exercise the movie details screen.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MovieDetailsIntegrationTest : BaseMovieDetailsIntegrationTest() {

    @Test
    fun shouldRenderMovieDetailsWhenSelectedInHomeScreen() {
        //-- we need results to navigate to details screen
        stubConfigurationDefault()
        stubNowPlayingFirstPage()
        stubMovieDetails()

        activityTestRule.launch()
        waitForMoviesLoadingDone()

        navigateToMovieDetails()
        waitForMovieDetailsLoaded()

        // scroll to the last position of the NestedScrollView to make sure all views are visible.
        onView(withId(R.id.detailCreditsSelectionView)).perform(NestedScrollViewExtension())

        onDetailsLoadingView().assertNotDisplayed()
        onDetailsErrorView().assertNotDisplayed()
        onDetailsContentView().assertDisplayed()

        //-- Action bar
        onActionBarTitle().assertWithText("Ad Astra")

        //-- Overview section
        onOverviewTitleView().assertDisplayed()
        onOverviewTitleView().assertWithText(R.string.overview_title)
        onOverviewContentView().assertDisplayed()
        onOverviewContentView().assertWithText("The near future, a time when both hope and hardships drive humanity to look to the stars and beyond. While a mysterious phenomenon menaces to destroy life on planet Earth, astronaut Roy McBride undertakes a mission across the immensity of space and its many perils to uncover the truth about a lost expedition that decades before boldly faced emptiness and silence in search of the unknown.")

        //-- Genres section
        onGenresList().assertDisplayed()
        onGenresList().assertItemCount(5)

        onView(withViewInRecyclerView(R.id.detailGenresRv, 0, R.id.genreListItemTxt))
                .check(matches(withText(R.string.sci_fi_genre)))
        onView(withViewInRecyclerView(R.id.detailGenresRv, 0, R.id.genreListItemIv))
                .check(matches(withDrawable(R.drawable.ic_science_ficcion)))

        onView(withViewInRecyclerView(R.id.detailGenresRv, 1, R.id.genreListItemTxt))
                .check(matches(withText(R.string.drama_genre)))
        onView(withViewInRecyclerView(R.id.detailGenresRv, 1, R.id.genreListItemIv))
                .check(matches(withDrawable(R.drawable.ic_drama)))

        onView(withViewInRecyclerView(R.id.detailGenresRv, 2, R.id.genreListItemTxt))
                .check(matches(withText(R.string.thriller_genre)))
        onView(withViewInRecyclerView(R.id.detailGenresRv, 2, R.id.genreListItemIv))
                .check(matches(withDrawable(R.drawable.ic_thriller)))

        onView(withViewInRecyclerView(R.id.detailGenresRv, 3, R.id.genreListItemTxt))
                .check(matches(withText(R.string.adventure_genre)))
        onView(withViewInRecyclerView(R.id.detailGenresRv, 3, R.id.genreListItemIv))
                .check(matches(withDrawable(R.drawable.ic_adventure)))

        onView(withViewInRecyclerView(R.id.detailGenresRv, 4, R.id.genreListItemTxt))
                .check(matches(withText(R.string.mystery_genre)))
        onView(withViewInRecyclerView(R.id.detailGenresRv, 4, R.id.genreListItemIv))
                .check(matches(withDrawable(R.drawable.ic_mystery)))

        //-- Popularity section
        onPopularityTitleView().assertDisplayed()
        onPopularityTitleView().assertWithText(R.string.popularity_title)
        onPopularityContentView().assertDisplayed()
        onPopularityContentView().assertWithText("752.439")

        //-- Vote count section
        onVoteCountTitleView().assertDisplayed()
        onVoteCountTitleView().assertWithText(R.string.vote_count_title)
        onVoteCountContentView().assertDisplayed()
        onVoteCountContentView().assertWithText("1703.0")

        //-- Release date section
        onReleaseTitleView().assertDisplayed()
        onReleaseTitleView().assertWithText(R.string.release_date_title)
        onReleaseContentView().assertDisplayed()
        onReleaseContentView().assertWithText("2019-09-17")

        onCreditsSelectionTitle().assertDisplayed()
        onCreditsSelectionTitle().assertWithText(R.string.movie_credits_title)
    }

    @Test
    fun shouldShowConnectivityError() {
        stubConfigurationDefault()
        stubNowPlayingFirstPage()

        activityTestRule.launch()
        waitForMoviesLoadingDone()

        activityTestRule.simulateNotConnectedToNetwork()
        navigateToMovieDetails()

        waitForMovieDetailsLoaded()

        onDetailsLoadingView().assertNotDisplayed()
        onDetailsContentView().assertNotDisplayed()

        onDetailsErrorView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_no_network_connection_message)
        onErrorViewButton().assertWithText(R.string.error_retry)
    }

    @Test
    fun shouldShowUnknownError() {
        stubConfigurationDefault()
        stubNowPlayingFirstPage()
        stubMovieDetailsWithError()

        activityTestRule.launch()
        waitForMoviesLoadingDone()

        navigateToMovieDetails()
        waitForMovieDetailsLoaded()

        onDetailsLoadingView().assertNotDisplayed()
        onDetailsContentView().assertNotDisplayed()

        onDetailsErrorView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_unexpected_error_message)
        onErrorViewButton().assertWithText(R.string.error_retry)
    }

    private fun onDetailsLoadingView() = onView(withId(R.id.movieDetailLoadingView))
    private fun onDetailsErrorView() = onView(withId(R.id.movieDetailErrorView))
    private fun onDetailsContentView() = onView(withId(R.id.movieDetailContent))
    private fun onOverviewTitleView() = onView(withId(R.id.detailOverviewTitleTxt))
    private fun onOverviewContentView() = onView(withId(R.id.detailOverviewContentTxt))
    private fun onPopularityTitleView() = onView(withId(R.id.detailPopularityTitleTxt))
    private fun onPopularityContentView() = onView(withId(R.id.detailPopularityContentTxt))
    private fun onVoteCountTitleView() = onView(withId(R.id.detailVoteCountTitleTxt))
    private fun onVoteCountContentView() = onView(withId(R.id.detailVoteCountContentTxt))
    private fun onReleaseTitleView() = onView(withId(R.id.detailReleaseDateTitleTxt))
    private fun onReleaseContentView() = onView(withId(R.id.detailReleaseDateContentTxt))
    private fun onCreditsSelectionTitle() = onView(withId(R.id.itemSelectionViewTitle))
    private fun onGenresList() = onView(withId(R.id.detailGenresRv))
}