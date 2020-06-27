package com.jpp.mpaccount.account.lists

import android.content.res.Resources
import android.os.Bundle

/**
 * The initialization parameter used for
 * [UserMovieListViewModel].
 */
internal data class UserMovieListParam(
    val section: UserMovieListType,
    val screenTitle: String,
    val posterSize: Int,
    val backdropSize: Int
) {
    companion object {
        fun fromArguments(
            arguments: Bundle?,
            resources: Resources,
            posterSize: Int,
            backdropSize: Int
        ): UserMovieListParam {
            val type = arguments?.get("listType") as UserMovieListType
            return UserMovieListParam(
                type,
                resources.getString(type.titleRes),
                posterSize,
                backdropSize
            )
        }
    }
}