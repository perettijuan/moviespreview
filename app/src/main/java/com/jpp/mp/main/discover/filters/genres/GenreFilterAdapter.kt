package com.jpp.mp.main.discover.filters.genres

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.databinding.GenreFilterItemBinding

class GenreFilterAdapter(private val itemSelectionListener: (GenreFilterItem) -> Unit) :
    RecyclerView.Adapter<GenreFilterAdapter.ViewHolder>() {

    private var itemList = mutableListOf<GenreFilterItem>()

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
        holder.bind(itemList[position], itemSelectionListener)
    }

    override fun getItemCount(): Int = itemList.size

    fun submitList(list: List<GenreFilterItem>) {
        if (list.isEmpty()) {
            return
        }

        if (list.size != itemList.size) {
            itemList = list.toMutableList()
            notifyDataSetChanged()
            return
        }

        for (i in list.indices) {
            if (list[i] != itemList[i]) {
                itemList[i] = list[i]
                notifyItemChanged(i)
            }
        }
    }

    class ViewHolder(
        private val itemBinding: GenreFilterItemBinding
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: GenreFilterItem, itemSelectionListener: (GenreFilterItem) -> Unit) {
            itemBinding.viewState = item
            itemView.setOnClickListener { itemSelectionListener(item) }
        }
    }
}