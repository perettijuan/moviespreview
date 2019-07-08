package com.jpp.mp.screens.main.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.ext.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_search_depreacted.*
import kotlinx.android.synthetic.main.list_item_search_deprecated.view.*
import javax.inject.Inject

class SearchFragmentDeprecated : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val searchItemSelectionListener: (SearchResultItem) -> Unit = {
        withViewModel { onSearchItemSelected(it) }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_depreacted, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchResultRv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SearchItemAdapter(searchItemSelectionListener)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        withViewModel {
            /*
             * ShowLoading this images in such a big configuration is only
             * to favor the transition to details
             */
            init(getScreenSizeInPixels().x)

            viewState().observe(this@SearchFragmentDeprecated.viewLifecycleOwner, Observer { viewState ->
                renderViewState(viewState)
            })

            navEvents().observe(this@SearchFragmentDeprecated.viewLifecycleOwner, Observer { navEvent ->
                navigateTo(navEvent)
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
                withRecyclerViewAdapter { clear() }
                renderIdle()
            }
            is SearchViewState.Searching -> {
                renderSearching()
            }
            is SearchViewState.ErrorUnknown -> {
                renderError()
                searchErrorView.asUnknownError { withViewModel { retryLastSearch() } }
            }
            is SearchViewState.ErrorUnknownWithItems -> {
                snackBarErrorUnknown(fragmentSearchRoot) { withViewModel { retryLastSearch() } }
                renderDoneSearching()
            }
            is SearchViewState.ErrorNoConnectivity -> {
                renderError()
                searchErrorView.asNoConnectivityError { withViewModel { retryLastSearch() } }
            }
            is SearchViewState.ErrorNoConnectivityWithItems -> {
                snackBarErrorNoConnectivity(fragmentSearchRoot) { withViewModel { retryLastSearch() } }
                renderDoneSearching()
            }
            is SearchViewState.EmptySearch -> {
//                emptySearch.text = String.format(getString(R.string.empty_search), viewState.searchText)
                renderEmptySearch()
            }
            is SearchViewState.DoneSearching -> {
                withRecyclerViewAdapter { submitList(viewState.pagedList) }
                renderDoneSearching()
            }
        }
    }

    /**
     * Handle navigation events.
     */
    private fun navigateTo(event: SearchViewNavigationEvent) {
        when (event) {
            is SearchViewNavigationEvent.ToMovieDetails -> {
                //TODO JPP need to navigate to details properly
                /*findNavController().navigate(

                         SearchFragmentDirections.actionSearchFragmentToMovieDetailsFragment()
                )*/
            }
            is SearchViewNavigationEvent.ToPerson -> {
                //TODO JPP need to navigate to person properly
                /*findNavController().navigate(
                        SearchFragmentDirections.actionSearchFragmentToPersonFragment(
                                event.personId,
                                event.personImageUrl,
                                event.personName
                        )
                )*/
            }
        }
    }


    private fun withViewModel(action: SearchFragmentViewModel.() -> Unit) {
        getViewModel<SearchFragmentViewModel>(viewModelFactory).action()
    }


    private fun withRecyclerViewAdapter(action: SearchItemAdapter.() -> Unit) {
        (searchResultRv.adapter as SearchItemAdapter).action()
    }

    private fun renderIdle() {
        emptySearch.setInvisible()
        searchErrorView.setInvisible()
        searchLoadingView.setInvisible()
        searchResultRv.setInvisible()

        searchPlaceHolderIv.setVisible()
    }

    private fun renderSearching() {
        searchPlaceHolderIv.setInvisible()
        emptySearch.setInvisible()
        searchErrorView.setInvisible()
        searchResultRv.setInvisible()

        searchLoadingView.setVisible()
    }

    private fun renderError() {
        searchPlaceHolderIv.setInvisible()
        emptySearch.setInvisible()
        searchLoadingView.setInvisible()
        searchResultRv.setInvisible()

        searchErrorView.setVisible()
    }

    private fun renderDoneSearching() {
        searchPlaceHolderIv.setInvisible()
        emptySearch.setInvisible()
        searchErrorView.setInvisible()
        searchLoadingView.setInvisible()

        searchResultRv.setVisible()
        searchResultRv.scheduleLayoutAnimation()
    }

    private fun renderEmptySearch() {
        searchPlaceHolderIv.setInvisible()
        searchErrorView.setInvisible()
        searchLoadingView.setInvisible()
        searchResultRv.setInvisible()

        emptySearch.setVisible()
    }

    /**
     * [PagedListAdapter] implementation to show the list of searchPage results.
     */
    class SearchItemAdapter(private val searchSelectionListener: (SearchResultItem) -> Unit) : PagedListAdapter<SearchResultItem, SearchItemAdapter.ViewHolder>(SearchResultDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_search_deprecated, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindSearchItem(it, searchSelectionListener)
            }
        }

        fun clear() {
            //Submitting a null paged list causes the adapter to remove all items in the RecyclerView
            submitList(null)
        }

        class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {

            fun bindSearchItem(searchItem: SearchResultItem, selectionListener: (SearchResultItem) -> Unit) {
                with(itemView) {
                    searchItemIv.loadImageUrlAsCircular(searchItem.imagePath)
                    searchItemTitleTxt.text = searchItem.name
                    searchItemTypeIv.setImageResource(searchItem.icon.iconRes)
                    setOnClickListener { selectionListener(searchItem) }
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