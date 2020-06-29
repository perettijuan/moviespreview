package com.jpp.mpaccount.account.lists

import androidx.recyclerview.widget.DiffUtil

internal class UserMovieDiffCallback : DiffUtil.ItemCallback<UserMovieItem>() {

    override fun areItemsTheSame(oldItem: UserMovieItem, newItem: UserMovieItem): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: UserMovieItem, newItem: UserMovieItem): Boolean {
        return oldItem.title == newItem.title
    }
}
