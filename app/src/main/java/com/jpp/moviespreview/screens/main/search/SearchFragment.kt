package com.jpp.moviespreview.screens.main.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.findViewById
import com.jpp.moviespreview.ext.getScreenSizeInPixels
import com.jpp.moviespreview.ext.getViewModel
import com.jpp.moviespreview.ext.loadImageUrlAsCircular
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.list_item_search.view.*
import javax.inject.Inject

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

        searchResultRv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SearchItemAdapter()
        }


        withSearchView { searchView ->
            searchView.setOnQueryTextListener(QuerySubmitter {
                withViewModel {
                    search(it).observe(this@SearchFragment.viewLifecycleOwner, Observer {
                        (searchResultRv.adapter as SearchItemAdapter).submitList(it)
                    })
                }
            })
        }

        findViewById<View>(androidx.appcompat.R.id.search_close_btn).setOnClickListener {
            withViewModel { clearSearch() }
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
            is SearchViewState.ErrorUnknownWithItems -> {
                R.layout.fragment_search_done
            }//TODO JPP show snackbar
            is SearchViewState.ErrorNoConnectivity -> {
                searchErrorView.asNoConnectivityError { }//TODO JPP
                R.layout.fragment_search_error
            }
            is SearchViewState.ErrorNoConnectivityWithItems -> {
                R.layout.fragment_search_done
            }//TODO JPP show snackbar
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


    class SearchItemAdapter : PagedListAdapter<SearchResultItem, SearchItemAdapter.ViewHolder>(SearchResultDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_search, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindSearchItem(it)
            }
        }


        class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {

            fun bindSearchItem(searchItem: SearchResultItem) {
                with(itemView) {
                    searchItemIv.loadImageUrlAsCircular(searchItem.imagePath)
                    searchItemTitleTxt.text = searchItem.name
                    searchItemTypeIv.setImageResource(searchItem.icon.iconRes)
                }
            }

        }

        class SearchResultDiffCallback : DiffUtil.ItemCallback<SearchResultItem>() {
            override fun areItemsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }


    /**
     * Inner [SearchView.OnQueryTextListener] implementation to handle the user search over the
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