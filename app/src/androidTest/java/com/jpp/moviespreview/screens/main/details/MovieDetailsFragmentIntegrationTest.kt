package com.jpp.moviespreview.screens.main.details

import android.os.Bundle
import android.view.WindowManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jpp.moviespreview.R
import com.jpp.moviespreview.assertions.*
import com.jpp.moviespreview.di.TestMPViewModelFactory
import com.jpp.moviespreview.extras.launch
import com.jpp.moviespreview.screens.EspressoTestCoroutineDispatchers
import com.jpp.moviespreview.testutils.FragmentTestActivity
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCase
import com.jpp.mpdomain.usecase.details.GetMovieDetailsResult
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests the interaction between the [MovieDetailsFragment] and the [MovieDetailsViewModel].
 */
@RunWith(AndroidJUnit4::class)
class MovieDetailsFragmentIntegrationTest {

    @get:Rule
    val activityTestRule = object : ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java, true, false) {

        override fun afterActivityLaunched() {
            runOnUiThread {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    private fun launchAndInjectFragment() {
        val fragment = MovieDetailsFragment().apply {
            arguments = Bundle().apply {
                putString("movieId", "11")
                putString("movieImageUrl", "anUrl")
                putString("movieTitle", "aTitle")
            }
        }
        activityTestRule.activity.startFragment(fragment, this@MovieDetailsFragmentIntegrationTest::inject)
    }

    private fun inject(fragment: MovieDetailsFragment) {
        fragment.viewModelFactory = TestMPViewModelFactory().apply {
            addVm(viewModel)
        }
    }

    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase

    @Before
    fun setUp() {
        getMovieDetailsUseCase = mockk()
        viewModel = MovieDetailsViewModel(
                dispatchers = EspressoTestCoroutineDispatchers(),
                getMovieDetailsUseCase = getMovieDetailsUseCase
        )

        activityTestRule.launch()
    }

    @Test
    fun shouldShowErrorUnknownView() {
        every { getMovieDetailsUseCase.getDetailsForMovie(any()) } returns GetMovieDetailsResult.ErrorUnknown

        launchAndInjectFragment()

        onDetailsContentView().assertNotDisplayed()
        onDetailsLoadingView().assertNotDisplayed()

        onDetailsErrorView().assertDisplayed()
        onView(withId(R.id.errorTitleTextView)).assertWithText(R.string.error_unexpected_error_message)
    }

    @Test
    fun shouldShowConnectivityError() {
        every { getMovieDetailsUseCase.getDetailsForMovie(any()) } returns GetMovieDetailsResult.ErrorNoConnectivity

        launchAndInjectFragment()

        onDetailsContentView().assertNotDisplayed()
        onDetailsLoadingView().assertNotDisplayed()

        onDetailsErrorView().assertDisplayed()
        onView(withId(R.id.errorTitleTextView)).assertWithText(R.string.error_no_network_connection_message)
    }

    @Test
    fun shouldShowMovieDetails() {
        val genres = listOf(
                MovieGenre(id = 28, name = "Action"),
                MovieGenre(id = 80, name = "Crime")
        )
        val domainMovieDetails = MovieDetail(
                id = 11.toDouble(),
                title = "aMovie",
                overview = "anOverview",
                release_date = "aReleaseDate",
                poster_path = "aPosterPath",
                genres = genres,
                vote_count = 200.toDouble(),
                vote_average = 100F,
                popularity = 1.2F
        )

        every { getMovieDetailsUseCase.getDetailsForMovie(any()) } returns GetMovieDetailsResult.Success(domainMovieDetails)

        launchAndInjectFragment()

        onDetailsErrorView().assertNotDisplayed()
        onDetailsLoadingView().assertNotDisplayed()

        onDetailsContentView().assertDisplayed()
        onOverviewView().assertWithText(domainMovieDetails.overview)
        onPopularityView().assertWithText(domainMovieDetails.popularity.toString())
        onVoteCountView().assertWithText(domainMovieDetails.vote_count.toString())
        onReleaseView().assertWithText(domainMovieDetails.release_date)
        onGenresView().assertItemCount(genres.size)

        onView(withViewInRecyclerView(R.id.detailsGenresRv, 0, R.id.genreListItemTxt))
                .check(ViewAssertions.matches(ViewMatchers.withText(genres[0].name)))

        onView(withViewInRecyclerView(R.id.detailsGenresRv, 1, R.id.genreListItemTxt))
                .check(ViewAssertions.matches(ViewMatchers.withText(genres[1].name)))
    }


    private fun onDetailsLoadingView() = onView(withId(R.id.detailsLoadingView))
    private fun onDetailsErrorView() = onView(withId(R.id.detailsErrorView))
    private fun onDetailsContentView() = onView(withId(R.id.fragmentDetailsContent))
    private fun onOverviewView() = onView(withId(R.id.detailsOverviewContentTxt))
    private fun onPopularityView() = onView(withId(R.id.detailsPopularityContentTxt))
    private fun onVoteCountView() = onView(withId(R.id.detailsVoteCountContentTxt))
    private fun onReleaseView() = onView(withId(R.id.detailsReleaseDateContentTxt))
    private fun onGenresView() = onView(withId(R.id.detailsGenresRv))

}