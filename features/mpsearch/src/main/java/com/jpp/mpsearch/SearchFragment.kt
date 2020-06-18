package com.jpp.mpsearch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mp.common.paging.MPVerticalPagingHandler
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpsearch.databinding.SearchFragmentBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Fragment used to provide search functionality to the application.
 * The user can perform a search on this Fragment in order to find either movies or characters/actors.
 *
 * This Fragment interacts with [SearchViewModel] in order to retrieve and show the results of a
 * search performed by the user. The ViewModel will perform the search, update the
 * UI states represented by [SearchViewState] and the Fragment will render those state updates.
 */
class SearchFragment : Fragment() {

    @Inject
    lateinit var searchViewModelFactory: SearchViewModelFactory

    private lateinit var viewBinding: SearchFragmentBinding

    private val viewModel: SearchViewModel by activityViewModels {
        MPGenericSavedStateViewModelFactory(
            searchViewModelFactory,
            this
        )
    }

    private var searchResultRv: RecyclerView? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

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

        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
    }

    override fun onDestroyView() {
        searchResultRv = null
        super.onDestroyView()
    }


    private fun setupViews(view: View) {
        searchResultRv = view.findViewById<RecyclerView>(R.id.searchResultRv).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SearchItemAdapter { item ->
                viewModel.onItemSelected(item)
            }

            val pagingHandler = MPVerticalPagingHandler(layoutManager as LinearLayoutManager) {
                viewModel.onNextPageRequested()
            }
            addOnScrollListener(pagingHandler)
        }
    }

    private fun renderViewState(viewState: SearchViewState) {
        viewBinding.viewState = viewState

        (searchResultRv?.adapter as SearchItemAdapter).submitList(viewState.contentViewState.searchResultList)
        //TODO
        //searchView?.setQuery(viewState.searchQuery, false)
        //searchView?.clearFocus() // hide keyboard
    }
}
