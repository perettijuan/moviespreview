package com.jpp.mpmoviedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mpmoviedetails.databinding.ListItemMovieDetailGenreBinding

internal class MovieDetailsGenreAdapter(private val genres: List<MovieGenreItem>) :
    RecyclerView.Adapter<MovieDetailsGenreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_movie_detail_genre,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(genres[position])

    override fun getItemCount() = genres.size

    class ViewHolder(private val itemBinding: ListItemMovieDetailGenreBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(genre: MovieGenreItem) {
            itemBinding.viewState = genre
            itemBinding.executePendingBindings()
        }
    }
}
