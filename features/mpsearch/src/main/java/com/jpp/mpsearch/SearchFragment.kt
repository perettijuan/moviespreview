package com.jpp.mpsearch

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mpsearch.SearchViewState.ShowSearchView
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SearchFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
         * Init with full screen width in order to provide proper
         * shared transitions.
         */
        withMainViewModel {
            viewStates.observe(this@SearchFragment.viewLifecycleOwner, Observer { it.actionIfNotHandled { viewState -> renderViewState(viewState) } })
            init(getScreenWidthInPixels())
        }
    }

    private fun renderViewState(searchViewState: SearchViewState) {
        when (searchViewState) {
            is ShowSearchView -> setUpSearchView()
        }
    }

    private fun withMainViewModel(action: SearchViewModel.() -> Unit) = withViewModel<SearchViewModel>(viewModelFactory) { action() }

    private fun setUpSearchView() {
        /*
        * The [SearchView] used to present a search option to the user belongs to the Activity
        * that contains this Fragment for a variety of reasons:
        * 1 - In order to provide back and forth navigation with the Android Architecture Components,
        * the application has only one Activity with different Fragments that are rendered in it.
        * 2 - To follow the design specs, the SearchView is provided in the Activity's action bar.
        */
        with(findSearchView(requireActivity().window.decorView as ViewGroup)) {
            queryHint = getString(R.string.search_hint)
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
     * Iterates recursively through the provided [ViewGroup] and finds a [SearchView],
     * if there's one.
     */
    private fun findSearchView(viewGroup: ViewGroup): SearchView {
        for (i in 0..viewGroup.childCount) {
            when (val view = viewGroup.getChildAt(i)) {
                is SearchView -> return view
                is ViewGroup -> return findSearchView(view)
            }
        }
        throw IllegalStateException("The container Activity for SearchFragment should provide a SearchView")
    }

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