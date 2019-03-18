package com.jpp.mp.assertions

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


/**
 * ViewAssertion to count the number of items in a RecyclerView.
 */
fun itemCount(expectedItemCount: Int): ViewAssertion = ViewAssertion { view, noViewFoundException ->
    if (noViewFoundException != null) {
        throw noViewFoundException
    }

    val rv = view as RecyclerView
    val adapter = rv.adapter
    assertThat(adapter?.itemCount, `is`(expectedItemCount))
}


/**
 * Matches a View in a RecyclerView at the given [position] with the provided [targetViewId].
 */
fun withViewInRecyclerView(recyclerViewId: Int,
                           position: Int,
                           targetViewId: Int): Matcher<View> = object : TypeSafeMatcher<View>() {

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