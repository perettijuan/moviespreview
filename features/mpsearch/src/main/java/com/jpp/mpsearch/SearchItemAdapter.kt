package com.jpp.mpsearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mpsearch.databinding.ListItemSearchBinding

/**
 * Internal [PagedListAdapter] to render the list of search results. The fact that this class is a
 * [PagedListAdapter] indicates that the paging library is being used. Another important
 * aspect of this class is that it uses Data Binding to update the UI, which differs from the
 * containing class.
 */
internal class SearchItemAdapter(private val searchSelectionListener: (SearchResultItem) -> Unit) :
    PagedListAdapter<SearchResultItem, SearchItemAdapter.ViewHolder>(SearchResultDiffCallback()) {

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
        getItem(position)?.let {
            holder.bindSearchItem(it, searchSelectionListener)
        }
    }

    class ViewHolder(private val itemBinding: ListItemSearchBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindSearchItem(
            searchItem: SearchResultItem,
            selectionListener: (SearchResultItem) -> Unit
        ) {
            with(itemBinding) {
                viewState = searchItem
                executePendingBindings()
            }
            itemView.setOnClickListener { selectionListener(searchItem) }
        }
    }

    class SearchResultDiffCallback : DiffUtil.ItemCallback<SearchResultItem>() {
        override fun areItemsTheSame(
            oldItem: SearchResultItem,
            newItem: SearchResultItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SearchResultItem,
            newItem: SearchResultItem
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
}