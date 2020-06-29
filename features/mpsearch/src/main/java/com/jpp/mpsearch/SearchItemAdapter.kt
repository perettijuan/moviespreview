package com.jpp.mpsearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mpdesign.adapters.MPRecyclerViewAdapter
import com.jpp.mpsearch.databinding.ListItemSearchBinding

internal class SearchItemAdapter(private val searchSelectionListener: (SearchResultItem) -> Unit) :
    MPRecyclerViewAdapter<SearchResultItem, SearchItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_search,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindSearchItem(getItem(position), searchSelectionListener)
    }

    class ViewHolder(private val itemBinding: ListItemSearchBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindSearchItem(
            searchItem: SearchResultItem,
            selectionListener: (SearchResultItem) -> Unit
        ) {
            itemBinding.viewState = searchItem
            itemBinding.executePendingBindings()
            itemView.setOnClickListener { selectionListener(searchItem) }
        }
    }
}
