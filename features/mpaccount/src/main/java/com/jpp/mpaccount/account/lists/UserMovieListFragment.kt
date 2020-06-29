package com.jpp.mpaccount.account.lists

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.paging.MPVerticalPagingHandler
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpaccount.R
import com.jpp.mpaccount.databinding.FragmentUserMovieListBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Base fragment used to show the list of movies that are related to the user's account.
 * The application can show movies related to user's account in three categories:
 * - Favorite
 * - Rated
 * - In Watchlist
 *
 * This Fragment shows the movies list based on the configuration that is sent as parameter when
 * created. It can show all three categories, based on such parameters.
 */
class UserMovieListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: UserMovieListViewModelFactory

    private var viewBinding: FragmentUserMovieListBinding? = null

    private val viewModel: UserMovieListViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            viewModelFactory,
            this
        )
    }

    private var userMoviesList: RecyclerView? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_movie_list, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.onInit(UserMovieListType.fromArguments(arguments))
    }

    override fun onDestroyView() {
        viewBinding = null
        userMoviesList = null
        super.onDestroyView()
    }

    private fun setUpViews(view: View) {
        userMoviesList = view.findViewById<RecyclerView>(R.id.userMoviesList)?.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = UserMoviesAdapter { item -> viewModel.onMovieSelected(item) }
            val pagingHandler = MPVerticalPagingHandler(layoutManager as LinearLayoutManager) {
                viewModel.onNextPageRequested()
            }
            addOnScrollListener(pagingHandler)
        }
    }

    private fun renderViewState(viewState: UserMovieListViewState) {
        setScreenTitle(getString(viewState.screenTitle))
        viewBinding?.viewState = viewState
        viewBinding?.executePendingBindings()
        (userMoviesList?.adapter as UserMoviesAdapter).submitList(viewState.contentViewState.movieList)
    }
}
