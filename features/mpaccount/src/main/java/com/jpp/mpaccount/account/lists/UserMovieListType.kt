package com.jpp.mpaccount.account.lists

import android.os.Bundle
import androidx.annotation.StringRes
import com.jpp.mpaccount.R

/**
 * Represents the type of user movie list that the application can
 * show.
 */
enum class UserMovieListType(@StringRes val titleRes: Int) {
    FAVORITE_LIST(R.string.user_account_favorite_title),
    RATED_LIST(R.string.user_account_rated_title),
    WATCH_LIST(R.string.user_account_watchlist_title);

    companion object {
        internal fun fromArguments(
            arguments: Bundle?
        ): UserMovieListType = arguments?.get("listType") as UserMovieListType
    }
}
