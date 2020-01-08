package com.jpp.mp.assertions

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

/**
 * [ViewAction] to type text into a [SearchView].
 * The provided [text] will be inserted directly in the [SearchView], but the query will
 * be not submitted.
 */
fun typeText(text: String): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            // Ensure that only apply if it is a SearchView and if it is visible.
            return CoreMatchers.allOf(ViewMatchers.isDisplayed(), ViewMatchers.isAssignableFrom(SearchView::class.java))
        }

        override fun getDescription(): String {
            return "Change view text"
        }

        override fun perform(uiController: UiController, view: View) {
            (view as SearchView).setQuery(text, false)
        }
    }
}

/**
 * [ViewAction] to type text into a [SearchView] and submite the query.
 * The provided [text] will be inserted directly in the [SearchView] and the query associated will
 * be submitted.
 */
fun typeTextAndSubmit(text: String): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            // Ensure that only apply if it is a SearchView and if it is visible.
            return CoreMatchers.allOf(ViewMatchers.isDisplayed(), ViewMatchers.isAssignableFrom(SearchView::class.java))
        }

        override fun getDescription(): String {
            return "Change view text"
        }

        override fun perform(uiController: UiController, view: View) {
            (view as SearchView).setQuery(text, true)
        }
    }
}
