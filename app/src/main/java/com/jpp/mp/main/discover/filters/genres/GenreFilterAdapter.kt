package com.jpp.mp.main.discover.filters.genres

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.databinding.GenreFilterItemBinding
import com.jpp.mpdesign.adapters.MPRecyclerViewAdapter

class GenreFilterAdapter : MPRecyclerViewAdapter<GenreFilterItem, GenreFilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.genre_filter_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val itemBinding: GenreFilterItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: GenreFilterItem) {
            itemBinding.viewState = item
        }
    }
}
