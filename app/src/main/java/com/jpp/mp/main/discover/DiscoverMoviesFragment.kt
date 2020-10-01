package com.jpp.mp.main.discover

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jpp.mp.R
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.paging.MPVerticalPagingHandler
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mp.databinding.FragmentDiscoverMoviesBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

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

    // used to restore the position of the RecyclerView on view re-creation
    // TODO we can simplify this once RecyclerView 1.2.0 is released
    //  ==> https://medium.com/androiddevelopers/restore-recyclerview-scroll-position-a8fbdc9a9334
    private var rvState: Parcelable? = null

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

        rvState = savedInstanceState?.getParcelable(DISCOVER_MOVIES_RV_STATE_KEY) ?: rvState

        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.filterViewState.observeValue(viewLifecycleOwner, ::renderFilterViewState)
        viewModel.onInit()
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    override fun onPause() {
        rvState = viewBinding?.discoverMovieList?.layoutManager?.onSaveInstanceState()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            DISCOVER_MOVIES_RV_STATE_KEY,
            viewBinding?.discoverMovieList?.layoutManager?.onSaveInstanceState()
        )
        super.onSaveInstanceState(outState)
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
        }
    }

    private fun renderViewState(viewState: DiscoverMoviesViewState) {
        setScreenTitle(getString(viewState.screenTitle))
        viewBinding?.viewState = viewState
        (viewBinding?.discoverMovieList?.adapter as DiscoverMoviesAdapter).submitList(viewState.contentViewState.itemList)
        viewBinding?.discoverMovieList?.layoutManager?.onRestoreInstanceState(rvState)
    }

    private fun renderFilterViewState(viewState: DiscoverMoviesFiltersViewState) {
        viewBinding?.filterViewState = viewState
    }

    private companion object {
        const val DISCOVER_MOVIES_RV_STATE_KEY = "discoverMoviesRvStateKey"
    }
}