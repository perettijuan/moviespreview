package com.jpp.mpaccount.account.lists

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
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
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewBinding: FragmentUserMovieListBinding

    // used to restore the position of the RecyclerView on view re-creation
    private var rvState: Parcelable? = null

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_movie_list, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvState = savedInstanceState?.getParcelable(USER_MOVIES_RV_STATE_KEY) ?: rvState

        withViewModel {
            viewStates.observe(this@UserMovieListFragment.viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState
                viewBinding.executePendingBindings()

                withRecyclerView {
                    layoutManager = LinearLayoutManager(requireActivity()).apply { onRestoreInstanceState(rvState) }
                    adapter = UserMoviesAdapter { item, position ->
                        withViewModel { onMovieSelected(item, position) }
                    }.apply { submitList(viewState.contentViewState.movieList) }
                }
            })

            navEvents.observe(this@UserMovieListFragment.viewLifecycleOwner, Observer { navEvent -> reactToNavEvent(navEvent) })

            currentSection.observe(viewLifecycleOwner, Observer { currentSection ->
                withNavigationViewModel(viewModelFactory) {
                    destinationReached(ReachedDestination(getString(currentSection.titleRes)))
                }
            })

            onInit(UserMovieListParam.fromArguments(arguments, getScreenWidthInPixels(), getScreenWidthInPixels()))
        }
    }

    override fun onPause() {
        withRecyclerView { rvState = layoutManager?.onSaveInstanceState() }
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        withRecyclerView { outState.putParcelable(USER_MOVIES_RV_STATE_KEY, layoutManager?.onSaveInstanceState()) }
        super.onSaveInstanceState(outState)
    }


    private fun withViewModel(action: UserMovieListViewModel.() -> Unit) = getViewModel<UserMovieListViewModel>(viewModelFactory).action()
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

    private companion object {
        const val USER_MOVIES_RV_STATE_KEY = "userMoviesRvStateKey"
    }
}