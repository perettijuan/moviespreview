package com.jpp.mp.extras

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.jpp.mp.R

/**
 * Performs the navigation from the home's movies list to the movie details section by
 * pressing the first item of the list.
 */
fun navigateToMovieDetails(): ViewInteraction = onView(withId(R.id.movieList))
        .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

/**
 * Performs the navigation from the movie details section to the movie credits section
 */
fun navigateToMovieCredits(): ViewInteraction = onView(withId(R.id.detailCreditsSelectionView))
        .perform(click())