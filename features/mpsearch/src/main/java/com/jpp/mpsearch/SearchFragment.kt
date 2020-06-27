package com.jpp.mpsearch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.paging.MPVerticalPagingHandler
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpsearch.databinding.SearchFragmentBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Fragment used to show the contents of a search. The user can interact with the SearchView provided
 * by [SearchActivity] in order to search for actors and/or movies. [SearchViewModel] performs the
 * search an updates the view state.
 */
class SearchFragment : Fragment() {

    @Inject
    lateinit var searchViewModelFactory: SearchViewModelFactory

    private var viewBinding: SearchFragmentBinding? = null

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
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        viewModel.contentViewState.observeValue(viewLifecycleOwner, ::renderViewState)
    }

    override fun onDestroyView() {
        viewBinding = null
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

    private fun renderViewState(viewState: SearchContentViewState) {
        viewBinding?.viewState = viewState
        (searchResultRv?.adapter as SearchItemAdapter).submitList(viewState.contentViewState.searchResultList)
    }
}
