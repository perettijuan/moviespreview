package com.jpp.mpsearch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment

class SearchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(requireActivity()) {
            findSearchView(window.decorView as ViewGroup)
        }
    }

    private fun findSearchView(viewGroup: ViewGroup) {
        for (i in 0..viewGroup.childCount) {
            when (val view = viewGroup.getChildAt(i)) {
                is SearchView -> setUpSearchView(view)
                is ViewGroup -> findSearchView(view)
            }
        }
    }

    private fun setUpSearchView(searchView: SearchView) {
        with(searchView) {
            isIconified = false
            setIconifiedByDefault(false)
            setOnQueryTextListener(QuerySubmitter { Log.d("JPPLOG", "Search $it") })
            findViewById<View>(androidx.appcompat.R.id.search_close_btn).setOnClickListener {
                TODO("clear search")
                //onSearchEvent
            }
        }
    }

    /*
     //TODO JPP move to SearchFragment
     private fun onSearchEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.ClearSearch -> {
                with(mainSearchView) {
                    setQuery("", false)
                    requestFocus()
                }
            }
            is SearchEvent.Search -> {
                with(mainSearchView) {
                    clearFocus()
                }
            }
        }
      }
     */
    /**
     * Inner [SearchView.OnQueryTextListener] implementation to handle the user searchPage over the
     * SearchView. It waits to submit the query a given amount of time that is based on the size
     * of the text introduced by the user.
     *
     * Note that this custom implementation could be a lot simpler using Android RxBindings, but
     * I don't want to bring RxJava into the project for this single reason.
     */
    private inner class QuerySubmitter(private val callback: (String) -> Unit) : SearchView.OnQueryTextListener {

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

}