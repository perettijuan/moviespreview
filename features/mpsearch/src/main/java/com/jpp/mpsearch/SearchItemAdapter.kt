package com.jpp.mpsearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mpsearch.databinding.ListItemSearchBinding

internal class SearchItemAdapter(private val searchSelectionListener: (SearchResultItem) -> Unit) :
    RecyclerView.Adapter<SearchItemAdapter.ViewHolder>() {

    private var itemList = ArrayList<SearchResultItem>()

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
        holder.bindSearchItem(itemList[position], searchSelectionListener)
    }

    override fun getItemCount(): Int = itemList.size

    fun submitList(newData: List<SearchResultItem>) {
        val currentLastIndex = itemList.size
        itemList.clear()
        itemList.addAll(newData)
        notifyItemRangeChanged(currentLastIndex, itemList.size)
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