package com.jpp.mpsearch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.withNavigationViewModel
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mpsearch.databinding.ListItemSearchBinding
import com.jpp.mpsearch.databinding.SearchFragmentBinding

/**
 * Fragment used to provide search functionality to the application.
 * The user can perform a search on this Fragment in order to find either movies or characters/actors.
 *
 * This Fragment interacts with [SearchViewModel] in order to retrieve and show the results of a
 * search performed by the user. The ViewModel will perform the search, update the
 * UI states represented by [SearchViewState] and the Fragment will render those state updates.
 */
class SearchFragment : MPFragment<SearchViewModel>() {

    private lateinit var viewBinding: SearchFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        withRecyclerView {
            layoutManager = LinearLayoutManager(activity)
            adapter = SearchItemAdapter { item, position ->
                withViewModel { onItemSelected(item, position) }
            }
        }
        setUpSearchView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        withViewModel {
            viewState.observe(this@SearchFragment.viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState

                withRecyclerView { (adapter as SearchItemAdapter).submitList(viewState.contentViewState.searchResultList) }

                withSearchView {
                    setQuery(viewState.searchQuery, false)
                    clearFocus() // hide keyboard
                }
            })
            navEvents.observe(this@SearchFragment.viewLifecycleOwner, Observer { it.actionIfNotHandled { navEvent -> reactToNavEvent(navEvent) } })
            onInit(getScreenWidthInPixels())
        }
    }


    private fun reactToNavEvent(navEvent: SearchNavigationEvent) {
        when (navEvent) {
            is SearchNavigationEvent.GoToMovieDetails -> withNavigationViewModel(viewModelFactory) { navigateToMovieDetails(navEvent.movieId, navEvent.movieImageUrl, navEvent.movieTitle) }
        }
    }

    override fun withViewModel(action: SearchViewModel.() -> Unit) = withViewModel<SearchViewModel>(viewModelFactory) { action() }

    private fun withRecyclerView(action: RecyclerView.() -> Unit) = view?.findViewById<RecyclerView>(R.id.searchResultRv)?.let(action)
    private fun withSearchView(action: SearchView.() -> Unit) {
        findSearchView(requireActivity().window.decorView as ViewGroup).action()
    }

    private fun setUpSearchView() {
        /*
        * The [SearchView] used to present a search option to the user belongs to the Activity
        * that contains this Fragment for a variety of reasons:
        * 1 - In order to provide back and forth navigation with the Android Architecture Components,
        * the application has only one Activity with different Fragments that are rendered in it.
        * 2 - To follow the design specs, the SearchView is provided in the Activity's action bar.
        */
        withSearchView {
            requestFocus()
            queryHint = getString(R.string.search_hint)
            isIconified = false
            setIconifiedByDefault(false)
            setOnQueryTextListener(QuerySubmitter { withViewModel { onSearch(it) } })
            findViewById<View>(androidx.appcompat.R.id.search_close_btn).setOnClickListener { withViewModel { onClearSearch() } }
        }
    }

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

    /**
     * Internal [PagedListAdapter] to render the list of search results. The fact that this class is a
     * [PagedListAdapter] indicates that the paging library is being used. Another important
     * aspect of this class is that it uses Data Binding to update the UI, which differs from the
     * containing class.
     */
    class SearchItemAdapter(private val searchSelectionListener: (SearchResultItem, Int) -> Unit) : PagedListAdapter<SearchResultItem, SearchItemAdapter.ViewHolder>(SearchResultDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.list_item_search,
                            parent,
                            false
                    )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindSearchItem(it, searchSelectionListener)
            }
        }

        class ViewHolder(private val itemBinding: ListItemSearchBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bindSearchItem(searchItem: SearchResultItem, selectionListener: (SearchResultItem, Int) -> Unit) {
                with(itemBinding) {
                    viewState = searchItem
                    executePendingBindings()
                }
                itemView.setOnClickListener { selectionListener(searchItem, adapterPosition) }
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