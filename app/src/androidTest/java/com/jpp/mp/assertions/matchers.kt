package com.jpp.mp.assertions

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.ListView
import android.widget.ScrollView
import androidx.annotation.IdRes
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
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
 * A [Matcher] that verifies if an [ImageView] contains a drawable resource identified by [resourceId].
 */
fun withDrawable(resourceId: Int): Matcher<View> = object : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("with drawable from resource id: ")
        description?.appendValue(resourceId)
    }

    override fun matchesSafely(target: View?): Boolean {
        if (target !is ImageView) {
            return false
        }
        if (resourceId < 0) {
            return target.drawable == null
        }
        val resources = target.getContext().resources
        val expectedDrawable = resources.getDrawable(resourceId) ?: return false
        val bitmap = getBitmap(target.drawable)
        val otherBitmap = getBitmap(expectedDrawable)
        return bitmap.sameAs(otherBitmap)
    }

    private fun getBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
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
 * Overrides the [ViewActions.scrollTo] method using delegation to add the [NestedScrollView] to the list of supported
 * classes by the original Espresso method.
 * Source: https://medium.com/@devasierra/espresso-nestedscrollview-scrolling-via-kotlin-delegation-5e7f0aa64c09
 */
class NestedScrollViewExtension(scrollToAction: ViewAction = ViewActions.scrollTo()) : ViewAction by scrollToAction {
    override fun getConstraints(): Matcher<View> {
        return Matchers.allOf(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                ViewMatchers.isDescendantOfA(Matchers.anyOf(ViewMatchers.isAssignableFrom(NestedScrollView::class.java),
                        ViewMatchers.isAssignableFrom(ScrollView::class.java),
                        ViewMatchers.isAssignableFrom(HorizontalScrollView::class.java),
                        ViewMatchers.isAssignableFrom(ListView::class.java))))
    }
}
