package com.jpp.mpaccount.account.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mpaccount.R
import com.jpp.mpaccount.databinding.ListItemUserMovieBinding
import com.jpp.mpdesign.adapters.MPRecyclerViewAdapter

internal class UserMoviesAdapter(private val itemSelectionListener: (UserMovieItem) -> Unit) :
    MPRecyclerViewAdapter<UserMovieItem, UserMoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_user_movie,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMovieItem(getItem(position), itemSelectionListener)
    }

    class ViewHolder(private val itemBinding: ListItemUserMovieBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindMovieItem(item: UserMovieItem, itemSelectionListener: (UserMovieItem) -> Unit) {
            itemBinding.viewState = item
            itemBinding.executePendingBindings()
            itemView.setOnClickListener { itemSelectionListener(item) }
        }
    }
}
