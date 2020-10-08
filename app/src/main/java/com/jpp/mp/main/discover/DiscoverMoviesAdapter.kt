package com.jpp.mp.main.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.databinding.ListItemDiscoverMovieBinding
import com.jpp.mpdesign.adapters.MPRecyclerViewAdapter

/**
 * [MPRecyclerViewAdapter] to show the list of discovered movies.
 */
class DiscoverMoviesAdapter(private val itemSelectionListener: (DiscoveredMovieListItem) -> Unit) :
    MPRecyclerViewAdapter<DiscoveredMovieListItem, DiscoverMoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_discover_movie,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), itemSelectionListener)
    }

    class ViewHolder(private val itemBinding: ListItemDiscoverMovieBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: DiscoveredMovieListItem, listener: (DiscoveredMovieListItem) -> Unit) {
            itemBinding.viewState = item
            itemView.setOnClickListener { listener(item) }
        }
    }
}
