package com.jpp.moviespreview.screens.main.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.findViewById
import dagger.android.support.AndroidSupportInjection
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

class SearchFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        withSearchView {
            it.setOnQueryTextListener(QuerySubmitter {
                Log.d("SEARCHME", "Search with $it")
            })
            findViewById<View>(androidx.appcompat.R.id.search_close_btn).setOnClickListener {
                //TODO JPP handle it
            }
        }
    }


    /**
     * The [SearchView] used in the Activity's action bar belongs to the container
     * activity (MainActivity). This function facilitates accessing the view that
     * belongs to the Activity view hierarchy.
     */
    private fun withSearchView(action: (SearchView) -> Unit) {
        with(findViewById<SearchView>(R.id.mainSearchView)) {
            action(this)
        }
    }


    /**
     * Inner [SearchView.OnQueryTextListener] implementation to handle the user search over the
     * SearchView. It waits to submit the query a given amount of time that is based on the size
     * of the text introduced by the user.
     */
    private inner class QuerySubmitter(private val callback: (String) -> Unit) : SearchView.OnQueryTextListener {

        var timer = Timer()

        override fun onQueryTextSubmit(query: String): Boolean {
            timer.cancel()
            if (!query.isEmpty()) {
                callback.invoke(query)
            }
            return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
            if (newText.length < 3) {
                return true
            }

            timer.cancel()

            val sleep = when (newText.length) {
                3 -> 1000L
                4, 5 -> 700L
                6, 7 -> 500L
                else -> 300L
            }
            timer = Timer()
            timer.schedule(sleep) {
                if (!newText.isEmpty()) {
                    callback.invoke(newText)
                }
            }
            return true
        }
    }

}