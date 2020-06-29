package com.jpp.mpabout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mpabout.databinding.ListItemAboutBinding

internal class AboutItemsAdapter(
    private val items: List<AboutItem>,
    private val itemSelectionListener: (AboutItem) -> Unit
) : RecyclerView.Adapter<AboutItemsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_about,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], itemSelectionListener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val itemBinding: ListItemAboutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(item: AboutItem, selectionListener: (AboutItem) -> Unit) {
            itemBinding.viewState = item
            itemBinding.executePendingBindings()
            itemView.setOnClickListener { selectionListener(item) }
        }
    }
}
