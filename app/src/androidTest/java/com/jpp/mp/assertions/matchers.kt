package com.jpp.mp.assertions

import android.content.res.Resources
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


/**
 * Matches a View in a RecyclerView at the given [position] with the provided [targetViewId].
 */
fun withViewInRecyclerView(
        recyclerViewId: Int,
        position: Int,
        targetViewId: Int
): Matcher<View> = object : TypeSafeMatcher<View>() {

    private lateinit var resources: Resources
    private var childView: View? = null

    override fun describeTo(description: Description) {
        description.appendText("With id: " + resources.getResourceName(recyclerViewId))
    }

    override fun matchesSafely(item: View): Boolean {
        resources = item.resources

        if (childView == null) {
            val rvView = item.rootView.findViewById<RecyclerView>(recyclerViewId)
            if (rvView != null && rvView.id == recyclerViewId) {
                childView = rvView.findViewHolderForAdapterPosition(position)?.itemView
            } else {
                return false
            }
        }

        return if (targetViewId == -1) {
            item == childView
        } else {
            item == childView?.findViewById(targetViewId)
        }
    }
}

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
    return disambiguatingMatcher(ViewMatchers.withId(viewId), index)
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


/**
 * [ViewAction] to type text into a [SearchView].
 * The provided [text] will be inserted directly in the [SearchView], but the query will
 * be not submitted.
 */
fun typeText(text: String): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            //Ensure that only apply if it is a SearchView and if it is visible.
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
