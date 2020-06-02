package com.jpp.mp.main.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.databinding.ListItemMovieBinding
import java.util.*

/**
 * Internal [PagedListAdapter] to render the list of movies. The fact that this class is a
 * [PagedListAdapter] indicates that the paging library is being used. Another important
 * aspect of this class is that it uses Data Binding to update the UI, which differs from the
 * containing class.
 */
class MoviesAdapter(private val movieSelectionListener: (MovieListItem) -> Unit) :
    RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    private val itemList = ArrayList<MovieListItem>()

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
        holder.bindMovie(itemList[position], movieSelectionListener)
    }

    override fun getItemCount(): Int = itemList.size

    fun updateDataList(newData: List<MovieListItem>) {
        val currentLastIndex = itemList.size
        itemList.addAll(newData)
        notifyItemRangeChanged(currentLastIndex, itemList.size)
    }

    class ViewHolder(private val itemBinding: ListItemMovieBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindMovie(movieList: MovieListItem, movieSelectionListener: (MovieListItem) -> Unit) {
            itemBinding.viewState = movieList
            itemView.setOnClickListener { movieSelectionListener(movieList) }
        }
    }
}
