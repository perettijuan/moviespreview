package com.jpp.moviespreview.assertions

import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers

/*
 * General view assertions.
 */
fun ViewInteraction.assertDisplayed() = check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
fun ViewInteraction.assertNotDisplayed() = check(ViewAssertions.matches(org.hamcrest.Matchers.not(ViewMatchers.isDisplayed())))
fun ViewInteraction.assertItemCount() = check(itemCount(20))