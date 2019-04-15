package com.jpp.mp.screens.main.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.ext.*
import com.jpp.mp.screens.main.favorites.UserMoviesFragmentDirections.toMovieDetails
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_user_movies.*
import kotlinx.android.synthetic.main.list_item_user_movies.view.*
import javax.inject.Inject

/**
 * TODO JPP -> Handle RefreshAppViewModel
 */
class UserMoviesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userMoviesList.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = UserMoviesAdapter {
                withViewModel { onItemSelected(it) }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        withViewModel {
            viewState().observe(this@UserMoviesFragment.viewLifecycleOwner, Observer { viewState -> renderViewState(viewState) })
            navEvents().observe(this@UserMoviesFragment.viewLifecycleOwner, Observer { navEvent -> navigateWith(navEvent) })
            fetchData(moviePosterSize = getScreenSizeInPixels().x,
                    movieBackdropSize = getScreenSizeInPixels().x)
        }
    }

    private fun withViewModel(action: UserMoviesViewModel.() -> Unit) = withViewModel<UserMoviesViewModel>(viewModelFactory) { action() }
    private fun withRecyclerViewAdapter(action: UserMoviesAdapter.() -> Unit) = (userMoviesList.adapter as UserMoviesAdapter).action()


    private fun renderViewState(viewState: UserMoviesViewState) {
        when (viewState) {
            is UserMoviesViewState.Refreshing -> {
                renderLoading()
                withViewModel {
                    fetchData(moviePosterSize = getScreenSizeInPixels().x,
                            movieBackdropSize = getScreenSizeInPixels().x)
                }
            }
            is UserMoviesViewState.Loading -> {
                renderLoading()
            }
            is UserMoviesViewState.NoMovies -> {
                TODO()
            }
            is UserMoviesViewState.UserNotLogged -> {
                TODO()
            }
            is UserMoviesViewState.ErrorNoConnectivity -> {
                userMoviesErrorView.asNoConnectivityError { withViewModel { retry() } }
                renderError()
            }
            is UserMoviesViewState.ErrorNoConnectivityWithItems -> {
                snackBarErrorNoConnectivity(userMoviesFragmentContent) { withViewModel { retry() } }
                renderContent()
            }
            is UserMoviesViewState.ErrorUnknown -> {
                userMoviesErrorView.asUnknownError { withViewModel { retry() } }
                renderError()
            }
            is UserMoviesViewState.ErrorUnknownWithItems -> {
                snackBarErrorUnknown(userMoviesFragmentContent) { withViewModel { retry() } }
                renderContent()
            }
            is UserMoviesViewState.InitialPageLoaded -> {
                withRecyclerViewAdapter { submitList(viewState.pagedList) }
                renderContent()
            }
        }
    }

    private fun navigateWith(navEvent: UserMoviesViewNavigationEvent) {
        when (navEvent) {
            is UserMoviesViewNavigationEvent.ToMovieDetails -> {
                findNavController().navigate(
                        toMovieDetails(navEvent.movieId, navEvent.movieImageUrl, navEvent.movieTitle)
                )
            }
        }
    }

    private fun renderLoading() {
        userMoviesList.setInvisible()
        userMoviesErrorView.setInvisible()

        userMoviesLoadingView.setVisible()
    }

    private fun renderError() {
        userMoviesList.setInvisible()
        userMoviesLoadingView.setInvisible()

        userMoviesErrorView.setVisible()
    }

    private fun renderContent() {
        userMoviesErrorView.setInvisible()
        userMoviesLoadingView.setInvisible()

        userMoviesList.setVisible()
    }


    class UserMoviesAdapter(private val itemSelectionListener: (UserMovieItem) -> Unit) : PagedListAdapter<UserMovieItem, UserMoviesAdapter.ViewHolder>(UserMovieDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.list_item_user_movies))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindMovieItem(it, itemSelectionListener)
            }
        }

        class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
            fun bindMovieItem(item: UserMovieItem, itemSelectionListener: (UserMovieItem) -> Unit) {
                with(itemView) {
                    userMovieListItemHeaderIcon.loadImageUrlAsCircular(item.headerImageUrl)
                    userMovieListItemTitle.text = item.title
                    userMovieListItemImage.loadImageUrl(item.contentImageUrl)
                    setOnClickListener { itemSelectionListener(item) }
                }
            }
        }
    }

    class UserMovieDiffCallback : DiffUtil.ItemCallback<UserMovieItem>() {

        override fun areItemsTheSame(oldItem: UserMovieItem, newItem: UserMovieItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: UserMovieItem, newItem: UserMovieItem): Boolean {
            return oldItem.title == newItem.title
        }
    }
}