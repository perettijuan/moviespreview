package com.jpp.mpsearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mp.common.paging.MPVerticalPagingHandler
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

    private var searchResultRv: RecyclerView? = null
    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)

        withViewModel {
            viewState.observe(this@SearchFragment.viewLifecycleOwner, Observer { viewState ->
                renderViewState(viewState)
            })
            onInit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchResultRv = null
        searchView = null
    }

    override fun withViewModel(action: SearchViewModel.() -> Unit) =
        withViewModel<SearchViewModel>(viewModelFactory) { action() }


    private fun setupViews(view: View) {
        searchResultRv = view.findViewById<RecyclerView>(R.id.searchResultRv).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SearchItemAdapter { item ->
                withViewModel { onItemSelected(item) }
            }

            val pagingHandler = MPVerticalPagingHandler(layoutManager as LinearLayoutManager) {
                withViewModel { onNextPageRequested() }
            }
            addOnScrollListener(pagingHandler)
        }

//        searchView = findSearchView(requireActivity().window.decorView as ViewGroup).apply {
//            /*
//             * The [SearchView] used to present a search option to the user belongs to the Activity
//             * that contains this Fragment for a variety of reasons:
//             * 1 - In order to provide back and forth navigation with the Android Architecture Components,
//             * the application has only one Activity with different Fragments that are rendered in it.
//             * 2 - To follow the design specs, the SearchView is provided in the Activity's action bar.
//             */
//            requestFocus()
//            queryHint = getString(R.string.search_hint)
//            isIconified = false
//            setIconifiedByDefault(false)
//            setOnQueryTextListener(QuerySubmitter { withViewModel { onSearch(it) } })
//            findViewById<View>(androidx.appcompat.R.id.search_close_btn).setOnClickListener { withViewModel { onClearSearch() } }
//        }
    }

    private fun renderViewState(viewState: SearchViewState) {
        viewBinding.viewState = viewState

        (searchResultRv?.adapter as SearchItemAdapter).submitList(viewState.contentViewState.searchResultList)
        searchView?.setQuery(viewState.searchQuery, false)
        searchView?.clearFocus() // hide keyboard
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
}
