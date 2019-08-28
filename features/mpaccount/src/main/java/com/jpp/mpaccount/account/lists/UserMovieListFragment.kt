package com.jpp.mpaccount.account.lists

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.getViewModel
import com.jpp.mp.common.extensions.withNavigationViewModel
import com.jpp.mp.common.navigation.Destination.ReachedDestination
import com.jpp.mpaccount.R
import com.jpp.mpaccount.account.lists.UserMovieListNavigationEvent.GoToMovieDetails
import com.jpp.mpaccount.account.lists.UserMovieListNavigationEvent.GoToUserAccount
import com.jpp.mpaccount.databinding.FragmentUserMovieListBinding
import com.jpp.mpaccount.databinding.ListItemUserMovieBinding
import com.jpp.mpdesign.ext.findViewInPositionWithId
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_user_movie_list.*
import kotlinx.android.synthetic.main.list_item_user_movie.view.*
import javax.inject.Inject

class UserMovieListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewBinding: FragmentUserMovieListBinding

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_movie_list, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        withRecyclerView {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = UserMoviesAdapter { item, position ->
                withViewModel { onMovieSelected(item, position) }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewStates.observe(this@UserMovieListFragment.viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState
                viewBinding.executePendingBindings()

                withRecyclerViewAdapter {
                    submitList(viewState.contentViewState.movieList)
                }
            })
            navEvents.observe(this@UserMovieListFragment.viewLifecycleOwner, Observer { navEvent -> reactToNavEvent(navEvent) })
            withUserMovieListType {
                when (it) {
                    UserMovieListType.FAVORITE_LIST -> onInitWithFavorites(getScreenWidthInPixels(), getScreenWidthInPixels())
                    UserMovieListType.RATED_LIST -> onInitWithRated(getScreenWidthInPixels(), getScreenWidthInPixels())
                    UserMovieListType.WATCH_LIST -> onInitWithWatchlist(getScreenWidthInPixels(), getScreenWidthInPixels())
                }
                // sync app bar title
                withNavigationViewModel(viewModelFactory) { destinationReached(ReachedDestination(getString(it.titleRes))) }
            }
        }
    }

    private fun withUserMovieListType(action: (UserMovieListType) -> Unit) {
        val args = arguments
                ?: throw IllegalStateException("You need to pass arguments to MovieDetailsFragment in order to show the content")

        action(args.get("listType") as UserMovieListType)
    }


    private fun withViewModel(action: UserMovieListViewModel.() -> Unit) = getViewModel<UserMovieListViewModel>(viewModelFactory).action()
    private fun withRecyclerViewAdapter(action: UserMoviesAdapter.() -> Unit) = withRecyclerView { (adapter as UserMoviesAdapter).action() }
    private fun withRecyclerView(action: RecyclerView.() -> Unit) = view?.findViewById<RecyclerView>(R.id.userMoviesList)?.let(action)

    /**
     * Reacts to the navigation event provided.
     */
    private fun reactToNavEvent(navEvent: UserMovieListNavigationEvent) {
        when (navEvent) {
            is GoToUserAccount -> withNavigationViewModel(viewModelFactory) { toPrevious() }
            is GoToMovieDetails -> {
                val view = userMoviesList.findViewInPositionWithId(navEvent.positionInList, R.id.listItemUserMovieMainImage)
                withNavigationViewModel(viewModelFactory) { navigateToMovieDetails(navEvent.movieId, navEvent.movieImageUrl, navEvent.movieTitle, view) }
            }
        }
    }


    class UserMoviesAdapter(private val itemSelectionListener: (UserMovieItem, Int) -> Unit) : PagedListAdapter<UserMovieItem, UserMoviesAdapter.ViewHolder>(UserMovieDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.list_item_user_movie,
                            parent,
                            false
                    )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindMovieItem(it, itemSelectionListener)
            }
        }

        class ViewHolder(private val itemBinding: ListItemUserMovieBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bindMovieItem(item: UserMovieItem, itemSelectionListener: (UserMovieItem, Int) -> Unit) {
                with(itemBinding) {
                    viewState = item
                    executePendingBindings()
                }
                with(itemView) {
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