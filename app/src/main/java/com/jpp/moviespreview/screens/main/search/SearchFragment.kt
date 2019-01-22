package com.jpp.moviespreview.screens.main.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionManager
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.findViewById
import com.jpp.moviespreview.ext.getScreenSizeInPixels
import com.jpp.moviespreview.ext.getViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_search.*
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
        withSearchView { searchView ->
            searchView.setOnQueryTextListener(QuerySubmitter {
                withViewModel {
                    search(it)
                }
            })
            findViewById<View>(androidx.appcompat.R.id.search_close_btn).setOnClickListener {
                withViewModel { clearSearch() }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        withViewModel {
            /*
             * Loading this images in such a big configuration is only
             * to favor the transition to details
             */
            init(getScreenSizeInPixels().x)

            viewState().observe(this@SearchFragment.viewLifecycleOwner, Observer { viewState ->
                renderViewState(viewState)
            })
        }
    }

    /**
     * Render the view state that the ViewModels indicates.
     * This method evaluates the viewState and applies a Transition from the
     * current state to a final state using a ConstraintLayout animation.
     */
    private fun renderViewState(viewState: SearchViewState) {
        when (viewState) {
            is SearchViewState.Idle -> {
                withSearchView {
                    it.setQuery("", false)
                    it.requestFocus()
                }
                R.layout.fragment_search
            }
            is SearchViewState.Searching -> {
                withSearchView { it.clearFocus() }
                R.layout.fragment_search_loading
            }
            is SearchViewState.ErrorUnknown -> {
                searchErrorView.asUnknownError { }//TODO JPP
                R.layout.fragment_search_error
            }
            is SearchViewState.ErrorUnknownWithItems -> {R.layout.fragment_search_done}//TODO JPP show snackbar
            is SearchViewState.ErrorNoConnectivity -> {
                searchErrorView.asNoConnectivityError { }//TODO JPP
                R.layout.fragment_search_error
            }
            is SearchViewState.ErrorNoConnectivityWithItems -> {R.layout.fragment_search_done}//TODO JPP show snackbar
            is SearchViewState.DoneSearching -> R.layout.fragment_search_done
        }.let {
            val constraint = ConstraintSet()
            constraint.clone(this@SearchFragment.context, it)
            TransitionManager.beginDelayedTransition(fragmentSearchRoot)
            constraint.applyTo(fragmentSearchRoot)
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

    private fun withViewModel(action: SearchViewModel.() -> Unit) {
        getViewModel<SearchViewModel>(viewModelFactory).action()
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
                    Handler(Looper.getMainLooper()).post { callback.invoke(newText) }
                }
            }
            return true
        }
    }

}