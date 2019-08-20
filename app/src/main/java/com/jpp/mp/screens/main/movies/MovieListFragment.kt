package com.jpp.mp.screens.main.movies

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
import com.jpp.mp.R
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.databinding.FragmentMovieListBinding
import com.jpp.mp.databinding.ListItemMovieBinding
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_movie_list.*
import kotlinx.android.synthetic.main.list_item_movie.view.*
import javax.inject.Inject

abstract class MovieListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewBinding: FragmentMovieListBinding

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_list, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewStates.observe(viewLifecycleOwner, Observer {
                it.actionIfNotHandled { viewState ->
                    viewBinding.viewState = viewState
                    movieList.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = MoviesAdapter { item, position -> TODO() }
                    }
                    withRecyclerViewAdapter { submitList(viewState.contentViewState.movieList) }
                }
            })

            initViewModel(
                    getScreenWidthInPixels(),
                    getScreenWidthInPixels(),
                    this)
        }
    }

    abstract fun initViewModel(posterSize: Int, backdropSize: Int, vm: MovieListViewModel)
    private fun withViewModel(action: MovieListViewModel.() -> Unit) = withViewModel<MovieListViewModel>(viewModelFactory) { action() }
    private fun withRecyclerViewAdapter(action: MoviesAdapter.() -> Unit) {
        (movieList.adapter as MoviesAdapter).action()
    }


    class MoviesAdapter(private val movieSelectionListener: (MovieItem, Int) -> Unit) : PagedListAdapter<MovieItem, MoviesAdapter.ViewHolder>(MovieDiffCallback()) {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.list_item_movie,
                            parent,
                            false
                    )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindMovie(it, movieSelectionListener)
            }
        }

        fun clear() {
            //Submitting a null paged list causes the adapter to remove all items in the RecyclerView
            submitList(null)
        }


        class ViewHolder(private val itemBinding: ListItemMovieBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bindMovie(movie: MovieItem, movieSelectionListener: (MovieItem, Int) -> Unit) {
                with(itemView) {
                    itemBinding.viewState = movie
                    movieItemImage.transitionName = "MovieImageAt$adapterPosition"
                    setOnClickListener { movieSelectionListener(movie, adapterPosition) }
                }
            }
        }
    }


    class MovieDiffCallback : DiffUtil.ItemCallback<MovieItem>() {

        override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem.title == newItem.title
        }
    }
}