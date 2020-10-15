package com.jpp.mp.main.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.databinding.ListItemMovieBinding
import com.jpp.mpdesign.adapters.MPRecyclerViewAdapter

class MoviesAdapter(private val movieSelectionListener: (MovieListItem) -> Unit) :
    MPRecyclerViewAdapter<MovieListItem, MoviesAdapter.ViewHolder>() {

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
        holder.bindMovie(getItem(position), movieSelectionListener)
    }

    class ViewHolder(private val itemBinding: ListItemMovieBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindMovie(movieList: MovieListItem, movieSelectionListener: (MovieListItem) -> Unit) {
            itemBinding.viewState = movieList
            itemView.setOnClickListener { movieSelectionListener(movieList) }
        }
    }
}
