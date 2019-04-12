package com.jpp.mp.screens.main.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.ext.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_user_movies.*
import kotlinx.android.synthetic.main.list_item_user_movies.view.*
import javax.inject.Inject

/**
 * TODO JPP -> paging
 * TODO JPP -> error handling with content
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
            adapter = UserMoviesAdapter()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        withViewModel {
            viewState().observe(this@UserMoviesFragment.viewLifecycleOwner, Observer { viewState ->
                renderViewState(viewState)
            })

            init(moviePosterSize = getScreenSizeInPixels().x,
                    movieBackdropSize = getScreenSizeInPixels().x)
        }
    }

    private fun withViewModel(action: UserMoviesViewModel.() -> Unit) = withViewModel<UserMoviesViewModel>(viewModelFactory) { action() }
    private fun withRecyclerViewAdapter(action: UserMoviesAdapter.() -> Unit) = (userMoviesList.adapter as UserMoviesAdapter).action()


    private fun renderViewState(viewState: UserMoviesViewState) {
        when (viewState) {
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
                TODO()
            }
            is UserMoviesViewState.ErrorNoConnectivityWithItems -> {
                TODO()
            }
            is UserMoviesViewState.ErrorUnknown -> {
                TODO()
            }
            is UserMoviesViewState.ErrorUnknownWithItems -> {
                TODO()
            }
            is UserMoviesViewState.InitialPageLoaded -> {
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

    private fun renderContent() {
        userMoviesErrorView.setInvisible()
        userMoviesLoadingView.setInvisible()

        userMoviesList.setVisible()
    }


    class UserMoviesAdapter : PagedListAdapter<UserMovieItem, UserMoviesAdapter.ViewHolder>(UserMovieDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.list_item_user_movies))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindMovieItem(it)
            }
        }

        class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
            fun bindMovieItem(item: UserMovieItem) {
                with(itemView) {
                    userMovieListItemHeaderIcon.loadImageUrlAsCircular(item.headerImageUrl)
                    userMovieListItemTitle.text = item.title
                    userMovieListItemImage.loadImageUrl(item.contentImageUrl)
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