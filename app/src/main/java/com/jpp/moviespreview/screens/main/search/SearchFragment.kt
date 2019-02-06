package com.jpp.moviespreview.screens.main.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.jpp.moviespreview.ext.getScreenSizeInPixels
import com.jpp.moviespreview.ext.getViewModel
import com.jpp.moviespreview.ext.loadImageUrlAsCircular
import com.jpp.moviespreview.ext.snackBar
import com.jpp.moviespreview.screens.main.SearchEvent
import com.jpp.moviespreview.screens.main.SearchViewViewModel
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


            searchResultRv.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    searchListUpdated(itemCount)
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                    searchListUpdated(itemCount)
                }
            })
        }


        /*
         * React to searchPage events posted by the MainActivity
         */
        withSearchViewViewModel {
            searchEvents().observe(this@SearchFragment.viewLifecycleOwner, Observer { event ->
                when (event) {
                    is SearchEvent.ClearSearch -> withViewModel { clearSearch() }
                    is SearchEvent.Search -> withViewModel {
                        search(event.query)
                    }
                }
            })
        }
    }

    /**
     * Render the view state that the ViewModels indicates.
     * This method evaluates the viewState and applies a Transition from the
     * current state to a final state using a ConstraintLayout animation.
     */
    private fun renderViewState(viewState: SearchViewState) {
        //TODO JPP -> improve this
        when (viewState) {
//            is SearchViewState.Idle -> {
//                (searchResultRv.adapter as SearchItemAdapter).clear()
//                R.layout.fragment_search
//            }
            is SearchViewState.Searching -> {
                (searchResultRv.adapter as SearchItemAdapter).submitList(viewState.pagedList)
                R.layout.fragment_search_loading
            }
            is SearchViewState.ErrorUnknown -> {
                searchErrorView.asUnknownError { withViewModel { retryLastSearch() } }
                R.layout.fragment_search_error
            }
//            is SearchViewState.ErrorUnknownWithItems -> {
//                snackBar(fragmentSearchRoot, R.string.error_unexpected_error_message, R.string.error_retry) {
//                    withViewModel { retryLastSearch() }
//                }
//                R.layout.fragment_search_done
//            }
            is SearchViewState.ErrorNoConnectivity -> {
                searchErrorView.asNoConnectivityError { withViewModel { retryLastSearch() } }
                R.layout.fragment_search_error
            }
//            is SearchViewState.ErrorNoConnectivityWithItems -> {
//                snackBar(fragmentSearchRoot, R.string.error_no_network_connection_message, R.string.error_retry) {
//                    withViewModel { retryLastSearch() }
//                }
//                R.layout.fragment_search_done
//            }
            is SearchViewState.DoneSearching -> R.layout.fragment_search_done
        }.let {
            val constraint = ConstraintSet()
            constraint.clone(this@SearchFragment.context, it)
            TransitionManager.beginDelayedTransition(fragmentSearchRoot)
            constraint.applyTo(fragmentSearchRoot)
        }
    }


    private fun withViewModel(action: SearchViewModel.() -> Unit) {
        getViewModel<SearchViewModel>(viewModelFactory).action()
    }

    /**
     * The SearchView that triggers the searchPage is in the MainActivity view hierarchy.
     * SearchViewViewModel allows the communication between this fragment and the SearchView
     * in the MainActivity.
     */
    private fun withSearchViewViewModel(action: SearchViewViewModel.() -> Unit) {
        getViewModel<SearchViewViewModel>(viewModelFactory).action()
    }


    /**
     * [PagedListAdapter] implementation to show the list of searchPage results.
     */
    class SearchItemAdapter : PagedListAdapter<SearchResultItem, SearchItemAdapter.ViewHolder>(SearchResultDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_search, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindSearchItem(it)
            }
        }

        fun clear() {
            //Submitting a null paged list causes the adapter to remove all items in the RecyclerView
            submitList(null)
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
}