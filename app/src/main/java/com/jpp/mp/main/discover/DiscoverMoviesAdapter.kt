package com.jpp.mp.main.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.databinding.ListItemDiscoverMovieBinding

class DiscoverMoviesAdapter(private val itemSelectionListener: (DiscoveredMovieListItem) -> Unit) :
    RecyclerView.Adapter<DiscoverMoviesAdapter.ViewHolder>() {

    private val itemList = ArrayList<DiscoveredMovieListItem>()

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
        holder.bind(itemList[position], itemSelectionListener)
    }

    override fun getItemCount(): Int = itemList.size

    fun updateDataList(newData: List<DiscoveredMovieListItem>) {
        val currentLastIndex = itemList.size
        itemList.addAll(newData)
        notifyItemRangeChanged(currentLastIndex, itemList.size)
    }

    class ViewHolder(private val itemBinding: ListItemDiscoverMovieBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: DiscoveredMovieListItem, listener: (DiscoveredMovieListItem) -> Unit) {
            itemBinding.viewState = item
            itemView.setOnClickListener { listener(item) }
        }
    }
}