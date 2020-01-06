package com.jpp.mp.screens.main.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.jpp.mp.R
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertItemCount
import com.jpp.mp.assertions.assertNotDisplayed
import com.jpp.mp.assertions.onActionBarBackButton
import com.jpp.mp.assertions.typeText
import com.jpp.mp.assertions.withDrawable
import com.jpp.mp.assertions.withViewInRecyclerView
import com.jpp.mp.stubbers.stubConfigurationDefault
import com.jpp.mp.stubbers.stubNowPlayingFirstPage
import com.jpp.mp.stubbers.stubSearchDefault
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Contains all the search integration tests cases of success searches.
 */
@RunWith(AndroidJUnit4::class)
class SearchSuccessTestCases : BaseSearchIntegrationTest() {

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
        onLoadingSearchView().assertDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onResultsRecyclerView().assertNotDisplayed()

        waitForSearchDone()
        onLoadingSearchView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onSearchPlaceHolderView().assertNotDisplayed()
        onResultsRecyclerView().assertDisplayed()
        onResultsRecyclerView().assertItemCount(19)

        // Verifying a couple of items should be enough

        // Item 0
        onView(withViewInRecyclerView(R.id.searchResultRv, 0, R.id.searchItemTitleTxt))
                .check(matches(withText("Rambo: Last Blood")))
        onView(withViewInRecyclerView(R.id.searchResultRv, 0, R.id.searchItemTypeIv))
                .check(matches(withDrawable(R.drawable.ic_clapperboard)))

        // Item 1
        onView(withViewInRecyclerView(R.id.searchResultRv, 1, R.id.searchItemTitleTxt))
                .check(matches(withText("Rambo III")))
        onView(withViewInRecyclerView(R.id.searchResultRv, 1, R.id.searchItemTypeIv))
                .check(matches(withDrawable(R.drawable.ic_clapperboard)))

        // Item 2
        onView(withViewInRecyclerView(R.id.searchResultRv, 2, R.id.searchItemTitleTxt))
                .check(matches(withText("Rambo")))
        onView(withViewInRecyclerView(R.id.searchResultRv, 2, R.id.searchItemTypeIv))
                .check(matches(withDrawable(R.drawable.ic_clapperboard)))
    }

    @Test
    fun shouldBringNextSearchPageWhenScrolling() {
        stubConfigurationDefault()
        stubSearchDefault()

        activityTestRule.launch()

        openSearchScreen()

        onSearchView().perform(typeText("Ramb"))

        waitForSearchStarted()
        onLoadingSearchView().assertDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onResultsRecyclerView().assertNotDisplayed()

        waitForSearchDone()
        onLoadingSearchView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onSearchPlaceHolderView().assertNotDisplayed()
        onResultsRecyclerView().assertDisplayed()
        onResultsRecyclerView().assertItemCount(19)

        // perform the scrolling
        onResultsRecyclerView().perform(scrollToPosition<RecyclerView.ViewHolder>(18))

        waitForMoreSearchResults()

        onResultsRecyclerView().assertItemCount(38)
    }

    @Test
    fun shouldKeepResultsWhenComingBack() {
        stubConfigurationDefault()
        stubSearchDefault()
        stubNowPlayingFirstPage() // we need a list in home to be consistent

        activityTestRule.launch()

        openSearchScreen()

        onSearchView().perform(typeText("Ramb"))

        //-- initial search
        waitForSearchStarted()
        waitForSearchDone()
        onLoadingSearchView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onSearchPlaceHolderView().assertNotDisplayed()
        onResultsRecyclerView().assertDisplayed()
        onResultsRecyclerView().assertItemCount(19)

        //-- go back home
        onActionBarBackButton().perform(click())
        waitForHomeScreen()

        //-- re-open search and verify data is there
        openSearchScreen()
        onLoadingSearchView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onSearchPlaceHolderView().assertNotDisplayed()
        onResultsRecyclerView().assertDisplayed()
        onResultsRecyclerView().assertItemCount(19)
    }

    @Test
    fun shouldClearSearchResultsWhenClearSearchIsPerformed() {
        stubConfigurationDefault()
        stubSearchDefault()

        activityTestRule.launch()

        openSearchScreen()

        onSearchView().perform(typeText("Ramb"))

        //-- initial search
        waitForSearchStarted()
        waitForSearchDone()
        onLoadingSearchView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onSearchPlaceHolderView().assertNotDisplayed()
        onResultsRecyclerView().assertDisplayed()
        onResultsRecyclerView().assertItemCount(19)

        onView(withId(androidx.appcompat.R.id.search_close_btn))
                .perform(click())

        onLoadingSearchView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onResultsRecyclerView().assertNotDisplayed()
        onResultsRecyclerView().assertItemCount(0)
        onSearchPlaceHolderView().assertDisplayed()
    }

    private fun waitForMoreSearchResults() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for loading done"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<RecyclerView>(R.id.searchResultRv).adapter?.let { it.itemCount > 19 }
                        ?: false
            }
        })
    }

    private fun waitForHomeScreen() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for loading done"

            override fun checkCondition(): Boolean {
                return activityTestRule.activity.findViewById<View>(R.id.movieList).visibility == View.VISIBLE
            }
        })
    }

}