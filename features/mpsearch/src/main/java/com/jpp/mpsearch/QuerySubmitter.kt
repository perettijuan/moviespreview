package com.jpp.mpsearch

import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.SearchView

/**
 * Inner [SearchView.OnQueryTextListener] implementation to handle the user searchPage over the
 * SearchView. It waits to submit the query a given amount of time that is based on the size
 * of the text introduced by the user.
 *
 * Note that this custom implementation could be a lot simpler using Android RxBindings, but
 * I don't want to bring RxJava into the project for this single reason.
 */
internal class QuerySubmitter(private val callback: (String) -> Unit) :
    SearchView.OnQueryTextListener {

    private lateinit var queryToSubmit: String
    private var isTyping = false
    private val typingTimeout = 1000L // 1 second
    private val timeoutHandler = Handler(Looper.getMainLooper())
    private val timeoutTask = Runnable {
        isTyping = false
        callback(queryToSubmit)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        timeoutHandler.removeCallbacks(timeoutTask)
        callback(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        timeoutHandler.removeCallbacks(timeoutTask)
        if (newText.length > 3) {
            queryToSubmit = newText
            timeoutHandler.postDelayed(timeoutTask, typingTimeout)
        }
        return true
    }
}
