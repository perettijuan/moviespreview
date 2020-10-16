package com.jpp.mp.main.discover

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jpp.mp.R
import com.jpp.mp.common.extensions.observeHandledEvent
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.paging.MPVerticalPagingHandler
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mp.databinding.FragmentDiscoverMoviesBinding
import com.jpp.mp.main.discover.filters.genres.GenreFilterItem
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Fragment used to show the list of discovered movies and allow the user to
 * filter by different criteria (at the moment the unique supported filter
 * is movie genres).
 */
class DiscoverMoviesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: DiscoverMoviesViewModelFactory

    private var viewBinding: FragmentDiscoverMoviesBinding? = null

    private val viewModel: DiscoverMoviesViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            viewModelFactory,
            this
        )
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_discover_movies,
            container,
            false
        )
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.filterViewState.observeValue(viewLifecycleOwner, ::renderFilterViewState)
        viewModel.events.observeHandledEvent(viewLifecycleOwner) { handleEvent() }
        viewModel.onInit()
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    private fun setUpViews() {
        viewBinding?.discoverMovieList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =
                DiscoverMoviesAdapter { itemSelected -> viewModel.onMovieSelected(itemSelected) }
            val pagingHandler = MPVerticalPagingHandler(layoutManager as LinearLayoutManager) {
                viewModel.onNextMoviePage()
            }
            addOnScrollListener(pagingHandler)
        }

        viewBinding?.discoverFiltersView?.actionListener = object : DiscoverMoviesSettingsView.ActionListener {
            override fun onExpandCollapseSelected(isExpanded: Boolean) {
                viewModel.onFilterExpandClicked(isExpanded)
            }

            override fun onGenreFilterItemSelected(genreFilterItem: GenreFilterItem) {
                viewModel.onGenreFilterItemSelected(genreFilterItem)
            }

            override fun onApplyFilterSelected() {
                viewModel.onApplyFiltersSelected()
            }

            override fun onClearAllSelected() {
                viewModel.onClearAllFiltersSelected()
            }
        }
    }

    private fun renderViewState(viewState: DiscoverMoviesViewState) {
        setScreenTitle(getString(viewState.screenTitle))
        viewBinding?.viewState = viewState
        (viewBinding?.discoverMovieList?.adapter as DiscoverMoviesAdapter).submitList(viewState.contentViewState.itemList)
    }

    private fun renderFilterViewState(viewState: DiscoverMoviesFiltersViewState) {
        viewBinding?.filterViewState = viewState
    }

    private fun handleEvent() {
        (viewBinding?.discoverMovieList?.adapter as DiscoverMoviesAdapter).clearList()
    }
}
