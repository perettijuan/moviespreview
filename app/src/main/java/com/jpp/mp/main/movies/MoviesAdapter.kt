package com.jpp.mp.main.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.databinding.ListItemMovieBinding

/**
 * Internal [PagedListAdapter] to render the list of movies. The fact that this class is a
 * [PagedListAdapter] indicates that the paging library is being used. Another important
 * aspect of this class is that it uses Data Binding to update the UI, which differs from the
 * containing class.
 */
class MoviesAdapter(private val movieSelectionListener: (MovieListItem) -> Unit) :
        ListAdapter<MovieListItem, MoviesAdapter.ViewHolder>(DIFFER) {

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

    class ViewHolder(private val itemBinding: ListItemMovieBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindMovie(movieList: MovieListItem, movieSelectionListener: (MovieListItem) -> Unit) {
            with(itemBinding) {
                viewState = movieList
                executePendingBindings()
            }
            itemView.setOnClickListener { movieSelectionListener(movieList) }
        }
    }


    companion object {
        val DIFFER = object : DiffUtil.ItemCallback<MovieListItem>() {

            override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}
