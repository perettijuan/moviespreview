package com.jpp.mpaccount.account.lists

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
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.getViewModel
import com.jpp.mp.common.extensions.withNavigationViewModel
import com.jpp.mpaccount.R
import com.jpp.mpaccount.account.lists.UserMovieListNavigationEvent.GoToUserAccount
import com.jpp.mpaccount.account.lists.UserMovieListViewState.*
import com.jpp.mpdesign.ext.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_user_movie_list.*
import kotlinx.android.synthetic.main.list_item_user_movie.view.*
import javax.inject.Inject
import com.jpp.mpaccount.account.lists.UserMovieListNavigationEvent.GoToMovieDetails

class UserMovieListFragment : Fragment() {

    enum class UserMovieListType {
        FAVORITE_LIST,
        RATED_LIST,
        WATCH_LIST
    }


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userMoviesList.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = UserMoviesAdapter { item, position ->
                withViewModel { onMovieSelected(item, position) }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args = arguments
                ?: throw IllegalStateException("You need to pass arguments to MovieDetailsFragment in order to show the content")

        withViewModel {
            viewStates.observe(this@UserMovieListFragment.viewLifecycleOwner, Observer { viewState -> viewState.actionIfNotHandled { renderViewState(it) } })
            navEvents.observe(this@UserMovieListFragment.viewLifecycleOwner, Observer { navEvent -> reactToNavEvent(navEvent) })
            when (args.get("listType") as UserMovieListType) {
                UserMovieListType.FAVORITE_LIST -> onInitWithFavorites(getScreenWidthInPixels(), getScreenWidthInPixels())
                UserMovieListType.RATED_LIST -> onInitWithRated(getScreenWidthInPixels(), getScreenWidthInPixels())
                UserMovieListType.WATCH_LIST -> onInitWithWatchlist(getScreenWidthInPixels(), getScreenWidthInPixels())
            }
        }
    }

    /**
     * Helper function to execute methods over the [UserMovieListViewModel].
     */
    private fun withViewModel(action: UserMovieListViewModel.() -> Unit) = getViewModel<UserMovieListViewModel>(viewModelFactory).action()

    private fun withRecyclerViewAdapter(action: UserMoviesAdapter.() -> Unit) = (userMoviesList.adapter as UserMoviesAdapter).action()


    /**
     * Performs the branching to render the proper views given then [viewState].
     */
    private fun renderViewState(viewState: UserMovieListViewState) {
        when (viewState) {
            is ShowNotConnected -> renderNotConnectedToNetwork()
            is ShowLoading -> renderLoading()
            is ShowError -> renderUnknownError()
            is ShowEmptyList -> TODO() //TODO JPP show empty list.
            is ShowMovieList -> {
                withRecyclerViewAdapter { submitList(viewState.pagedList) }
                renderContent()
            }
        }
    }

    private fun renderLoading() {
        userMoviesList.setInvisible()
        userMoviesErrorView.setInvisible()

        userMoviesLoadingView.setVisible()
    }

    private fun renderNotConnectedToNetwork() {
        userMoviesList.setInvisible()
        userMoviesLoadingView.setInvisible()

        userMoviesErrorView.asNoConnectivityError { withViewModel { onRetry() } }
        userMoviesErrorView.setVisible()
    }

    private fun renderUnknownError() {
        userMoviesList.setInvisible()
        userMoviesLoadingView.setInvisible()

        userMoviesErrorView.asUnknownError { withViewModel { onRetry() } }
        userMoviesErrorView.setVisible()
    }

    private fun renderContent() {
        userMoviesLoadingView.setInvisible()
        userMoviesErrorView.setInvisible()

        userMoviesList.setVisible()
    }

    /**
     * Reacts to the navigation event provided.
     */
    private fun reactToNavEvent(navEvent: UserMovieListNavigationEvent) {
        when (navEvent) {
            is GoToUserAccount -> findNavController().popBackStack()
            is GoToMovieDetails -> {
                val view = userMoviesList.findViewInPositionWithId(navEvent.positionInList, R.id.listItemUserMovieMainImage)
                withNavigationViewModel(viewModelFactory) { navigateToMovieDetails(navEvent.movieId, navEvent.movieImageUrl, navEvent.movieTitle, view) }
            }
        }
    }


    class UserMoviesAdapter(private val itemSelectionListener: (UserMovieItem, Int) -> Unit) : PagedListAdapter<UserMovieItem, UserMoviesAdapter.ViewHolder>(UserMovieDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.list_item_user_movie))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindMovieItem(it, itemSelectionListener)
            }
        }

        class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
            fun bindMovieItem(item: UserMovieItem, itemSelectionListener: (UserMovieItem, Int) -> Unit) {
                with(itemView) {
                    listItemUserMovieHeaderImage.loadImageUrlAsCircular(item.headerImageUrl)
                    listItemUserMovieTitle.text = item.title
                    listItemUserMovieMainImage.loadImageUrl(item.contentImageUrl)
                    listItemUserMovieMainImage.transitionName = "MovieImageAt$adapterPosition"
                    setOnClickListener { itemSelectionListener(item, adapterPosition) }
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