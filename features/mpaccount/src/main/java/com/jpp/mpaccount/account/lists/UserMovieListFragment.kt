package com.jpp.mpaccount.account.lists

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mp.common.paging.MPVerticalPagingHandler
import com.jpp.mpaccount.R
import com.jpp.mpaccount.databinding.FragmentUserMovieListBinding

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
class UserMovieListFragment : MPFragment<UserMovieListViewModel>() {

    private var viewBinding: FragmentUserMovieListBinding? = null

    private var userMoviesList: RecyclerView? = null

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
        withViewModel {
            viewState.observeValue(viewLifecycleOwner, ::renderViewState)
            onInit(
                UserMovieListParam.fromArguments(
                    arguments,
                    resources,
                    getScreenWidthInPixels(),
                    getScreenWidthInPixels()
                )
            )
        }
    }

    override fun onDestroyView() {
        viewBinding = null
        userMoviesList = null
        super.onDestroyView()
    }

    override fun withViewModel(action: UserMovieListViewModel.() -> Unit) =
        withViewModel<UserMovieListViewModel>(viewModelFactory) { action() }

    private fun setUpViews(view: View) {
        userMoviesList = view.findViewById<RecyclerView>(R.id.userMoviesList)?.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = UserMoviesAdapter { item -> withViewModel { onMovieSelected(item) } }
            val pagingHandler = MPVerticalPagingHandler(layoutManager as LinearLayoutManager) {
                withViewModel { onNextPageRequested() }
            }
            addOnScrollListener(pagingHandler)
        }
    }

    private fun renderViewState(viewState: UserMovieListViewState) {
        viewBinding?.viewState = viewState
        viewBinding?.executePendingBindings()
        (userMoviesList?.adapter as UserMoviesAdapter).submitList(viewState.contentViewState.movieList)
    }
}
