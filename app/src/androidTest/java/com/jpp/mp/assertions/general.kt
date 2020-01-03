package com.jpp.mp.assertions

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.jpp.mp.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

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
fun onErrorViewButton() = onView(withId(R.id.errorActionButton))

/**
 *
 * Disambiguates two views with the same ID by their relative ordering
 * in the view hierarchy.
 * @param viewId the id of the view that should be disambiguated
 * @param index the index of the specific view you want in relation to all
 * other views with the same id
 * @return a [Matcher]&lt;[View]&gt; that can disambiguate
 * multiple views with the same id
 * @see .disambiguatingMatcher
 */
fun disambiguateId(@IdRes viewId: Int, index: Int): Matcher<View> {
    return disambiguatingMatcher(withId(viewId), index)
}

/**
 *
 * Lifted from [Stack Overflow Link](https://stackoverflow.com/questions/29378552/in-espresso-how-to-choose-one-the-view-who-have-same-id-to-avoid-ambiguousviewm)
 *
 * Use this when you have multiple views on the screen that match some matcher in order to
 * perform some check or some action on a view at an index.
 * @param matcher The matcher that matches more than one view on the screen
 * @param index the index order of the view matching the matcher you pass in. The index will
 * correspond to the view's appearance in the view tree.
 * @return a [Matcher] capable of disambiguating the matcher passed in given an index
 */
fun disambiguatingMatcher(matcher: Matcher<View>, index: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        var currentIndex = 0

        override fun describeTo(description: Description) {
            description.appendText("with index: ")
            description.appendValue(index)
            matcher.describeTo(description)
        }

        override fun matchesSafely(view: View): Boolean {
            return matcher.matches(view) && currentIndex++ == index
        }
    }
}
