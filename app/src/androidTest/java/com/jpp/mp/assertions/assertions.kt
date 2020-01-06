package com.jpp.mp.assertions

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.jpp.mp.R
import org.hamcrest.CoreMatchers


/*
 * General view assertions.
 */
fun ViewInteraction.assertDisplayed(): ViewInteraction = check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
fun ViewInteraction.assertNotDisplayed(): ViewInteraction = check(ViewAssertions.matches(org.hamcrest.Matchers.not(ViewMatchers.isDisplayed())))
fun ViewInteraction.assertItemCount(count: Int): ViewInteraction = check(itemCount(count))
fun ViewInteraction.assertWithText(@StringRes stringRes: Int): ViewInteraction = check(ViewAssertions.matches(ViewMatchers.withText(stringRes)))
fun ViewInteraction.assertWithText(value: String): ViewInteraction = check(ViewAssertions.matches(ViewMatchers.withText(value)))

/*
 * Common view interactions
 */
fun onErrorViewText(): ViewInteraction = onView(withId(R.id.errorTitleTextView))
fun onErrorViewButton(): ViewInteraction = onView(withId(R.id.errorActionButton))
fun onActionBarBackButton(): ViewInteraction = onView(withContentDescription(R.string.abc_action_bar_up_description))


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