package com.jpp.mpsearch

import androidx.navigation.NavController

interface SearchNavigator {
    fun bind(newNavController: NavController)
    fun unBind()

    fun navigateToMovieDetails(
        movieId: String,
        movieImageUrl: String,
        movieTitle: String
    )

    fun navigateToPersonDetail(
        personId: String,
        personImageUrl: String,
        personName: String
    )
}
