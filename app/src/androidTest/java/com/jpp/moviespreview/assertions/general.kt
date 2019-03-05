package com.jpp.moviespreview.assertions

import androidx.annotation.StringRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.jpp.moviespreview.R

/*
 * General view assertions.
 */
fun ViewInteraction.assertDisplayed() = check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
fun ViewInteraction.assertNotDisplayed() = check(ViewAssertions.matches(org.hamcrest.Matchers.not(ViewMatchers.isDisplayed())))
fun ViewInteraction.assertItemCount(count: Int) = check(itemCount(count))
fun ViewInteraction.assertWithText(@StringRes stringRes: Int) = check(ViewAssertions.matches(ViewMatchers.withText(stringRes)))
fun ViewInteraction.assertWithText(value: String) = check(ViewAssertions.matches(ViewMatchers.withText(value)))


/*
 * Common view actions
 */
fun onErrorViewText() = onView(withId(R.id.errorTitleTextView))