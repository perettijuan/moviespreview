package com.jpp.mpdesign.adapters

import androidx.recyclerview.widget.RecyclerView

/**
 * A [RecyclerView.Adapter] that simplifies the implementation and the paging mechanism
 * by maintaining an internal list of all items.
 */
abstract class MPRecyclerViewAdapter<T, RV : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<RV>() {

    private var itemList = ArrayList<T>()

    override fun getItemCount(): Int = itemList.size

    fun submitList(newData: List<T>) {
        val currentLastIndex = itemList.size
        itemList.clear()
        itemList.addAll(newData)
        notifyItemRangeChanged(currentLastIndex, itemList.size)
    }

    fun getItem(position: Int): T = itemList[position]
}
