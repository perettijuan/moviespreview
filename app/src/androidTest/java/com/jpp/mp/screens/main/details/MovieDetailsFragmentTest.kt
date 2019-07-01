package com.jpp.mp.screens.main.details

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jpp.mp.R
import com.jpp.mp.assertions.*
import com.jpp.mp.di.TestMPViewModelFactory
import com.jpp.mp.extras.launch
import com.jpp.mp.testutils.FragmentTestActivity
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//TODO JPP re-enable espresso tests
@RunWith(AndroidJUnit4::class)
class MovieDetailsFragmentTest {

//    @get:Rule
//    val activityTestRule = object : ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java, true, false) {}
//
//    private fun launchAndInjectFragment() {
//        val fragment = MovieDetailsFragment().apply {
//            arguments = Bundle().apply {
//                putString("movieId", "11")
//                putString("movieImageUrl", "anUrl")
//                putString("movieTitle", "aTitle")
//            }
//        }
//        activityTestRule.activity.startFragment(fragment, this@MovieDetailsFragmentTest::inject)
//    }
//
//    private fun inject(fragment: MovieDetailsFragment) {
//
////        val viewModel = mockk<MovieDetailsViewModel>(relaxed = true)
////        every { viewModel.viewState() } returns viewStateLiveData
////        every { actionsViewModel.actionsState() } returns actionsStateLiveData
////        fragment.viewModelFactory = TestMPViewModelFactory().apply {
////            addVm(viewModel)
////            addVm(actionsViewModel)
////        }
//    }
//
//    private val viewStateLiveData = MutableLiveData<MovieDetailsViewState>()
//    private val actionsViewModel = mockk<MovieActionsViewModel>(relaxed = true)
//    private val actionsStateLiveData = MutableLiveData<MovieActionsState>()
//
//    @Before
//    fun setUp() {
//        activityTestRule.launch()
//    }
//
//    @Test
//    fun shouldShowLoading() {
//        launchAndInjectFragment()
//
//        viewStateLiveData.postValue(MovieDetailsViewState.Loading)
//
//        onDetailsContentView().assertNotDisplayed()
//        onDetailsErrorView().assertNotDisplayed()
//
//        onDetailsLoadingView().assertDisplayed()
//    }
//
//    @Test
//    fun shouldShowErrorUnknownView() {
//        launchAndInjectFragment()
//
//        viewStateLiveData.postValue(MovieDetailsViewState.ErrorUnknown)
//
//        onDetailsContentView().assertNotDisplayed()
//        onDetailsLoadingView().assertNotDisplayed()
//
//        onDetailsErrorView().assertDisplayed()
//        onErrorViewText().assertWithText(R.string.error_unexpected_error_message)
//    }
//
//    @Test
//    fun shouldShowConnectivityError() {
//        launchAndInjectFragment()
//
//        viewStateLiveData.postValue(MovieDetailsViewState.ErrorNoConnectivity)
//
//        onDetailsContentView().assertNotDisplayed()
//        onDetailsLoadingView().assertNotDisplayed()
//
//        onDetailsErrorView().assertDisplayed()
//        onErrorViewText().assertWithText(R.string.error_no_network_connection_message)
//    }
//
//    @Test
//    fun shouldShowMovieDetails() {
////        val genres = listOf(
////                MovieGenreItem.Action,
////                MovieGenreItem.Crime
////        )
////        val movieDetails = UiMovieDetails(
////                title = "aMovie",
////                overview = "anOverview",
////                releaseDate = "aReleaseDate",
////                genres = genres,
////                voteCount = 200.toDouble(),
////                voteAverage = 100F,
////                popularity = 1.2F
////        )
////
////        launchAndInjectFragment()
////
////        viewStateLiveData.postValue(MovieDetailsViewState.ShowDetail(movieDetails))
////
////        onDetailsErrorView().assertNotDisplayed()
////        onDetailsLoadingView().assertNotDisplayed()
////
////        onDetailsContentView().assertDisplayed()
////        onOverviewView().assertWithText(movieDetails.overview)
////        onPopularityView().assertWithText(movieDetails.popularity.toString())
////        onVoteCountView().assertWithText(movieDetails.voteCount.toString())
////        onReleaseView().assertWithText(movieDetails.releaseDate)
////        onGenresView().assertItemCount(genres.size)
////
////        onView(withViewInRecyclerView(R.id.detailsGenresRv, 0, R.id.genreListItemTxt))
////                .check(ViewAssertions.matches(ViewMatchers.withText(genres[0].name)))
////
////        onView(withViewInRecyclerView(R.id.detailsGenresRv, 1, R.id.genreListItemTxt))
////                .check(ViewAssertions.matches(ViewMatchers.withText(genres[1].name)))
//    }
//
//    @Test
//    fun shouldHideActionsButton() {
//        launchAndInjectFragment()
//
//        actionsStateLiveData.postValue(MovieActionsState.Hidden)
//
//        onFavoritesButton().assertNotDisplayed()
//    }
//
//
//    private fun onDetailsLoadingView() = onView(withId(R.id.detailsLoadingView))
//    private fun onDetailsErrorView() = onView(withId(R.id.detailsErrorView))
//    private fun onDetailsContentView() = onView(withId(R.id.fragmentDetailsContent))
//    private fun onOverviewView() = onView(withId(R.id.detailsOverviewContentTxt))
//    private fun onPopularityView() = onView(withId(R.id.detailsPopularityContentTxt))
//    private fun onVoteCountView() = onView(withId(R.id.detailsVoteCountContentTxt))
//    private fun onReleaseView() = onView(withId(R.id.detailsReleaseDateContentTxt))
//    private fun onGenresView() = onView(withId(R.id.detailsGenresRv))
//    private fun onFavoritesButton() = onView(withId(R.id.favActionButton))

}