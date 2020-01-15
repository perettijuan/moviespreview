package com.jpp.mp.assertions

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.jpp.mp.R
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf

/*
 * General view assertions.
 */
fun ViewInteraction.assertDisplayed(): ViewInteraction = check(matches(isDisplayed()))
fun ViewInteraction.assertNotDisplayed(): ViewInteraction = check(matches(org.hamcrest.Matchers.not(isDisplayed())))
fun ViewInteraction.assertItemCount(count: Int): ViewInteraction = check(itemCount(count))
fun ViewInteraction.assertWithText(@StringRes stringRes: Int): ViewInteraction = check(matches(withText(stringRes)))
fun ViewInteraction.assertWithText(value: String): ViewInteraction = check(matches(withText(value)))
fun ViewInteraction.assertClickable(): ViewInteraction = check(matches(isClickable()))

/*
 * Common view interactions
 */
fun onErrorViewText(): ViewInteraction = onView(withId(R.id.errorTitleTextView))
fun onErrorViewButton(): ViewInteraction = onView(withId(R.id.errorActionButton))
fun onActionBarBackButton(): ViewInteraction = onView(withContentDescription(R.string.abc_action_bar_up_description))
fun onActionBarTitle(): ViewInteraction = onView(allOf(instanceOf(TextView::class.java), withParent(withId(R.id.mainToolbar))))

/**
 * Asserts that a given [RecyclerView] is showing the [expectedItemCount] number of items.
 */
fun itemCount(expectedItemCount: Int): ViewAssertion = ViewAssertion { view, noViewFoundException ->
    if (noViewFoundException != null) {
        throw noViewFoundException
    }

    val rv = view as RecyclerView
    val adapter = rv.adapter
    ViewMatchers.assertThat(adapter?.itemCount, CoreMatchers.`is`(expectedItemCount))
}
