package com.jpp.mp.main

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.widget.Toolbar
import com.jpp.mp.R
import com.jpp.mpsearch.SearchActivity

/**
 * Delegate to perform the navigation from [MainActivity] to [SearchActivity].
 * The navigation between these two Activities involves a shared element transition
 * that, at the moment of the creation of this delegate, could not be implemented
 * with the Android Navigation Components. Therefore, this delegate has the responsibility
 * of performing the navigation by adding an extra configuration to perform the
 * shared element transitions.
 */
internal class MainToSearchNavigationDelegate(
    private var activity: MainActivity?,
    private var transitionToolbar: Toolbar?
) {

    fun onNavigateToSearch(): Boolean {
        val hostActivity = activity ?: return false
        val transitionView = transitionToolbar ?: return false

        val intent = Intent(hostActivity, SearchActivity::class.java)
        val transitionOptions = ActivityOptions.makeSceneTransitionAnimation(
            hostActivity,
            transitionView,
            hostActivity.getString(R.string.toolbar_search_transition)
        ).toBundle()
        hostActivity.startActivity(intent, transitionOptions)

        return true
    }

    fun onDestroy() {
        activity = null
        transitionToolbar = null
    }
}
