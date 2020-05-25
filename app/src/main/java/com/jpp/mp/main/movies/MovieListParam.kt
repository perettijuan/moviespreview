package com.jpp.mp.main.movies

import android.content.res.Resources
import com.jpp.mp.R
import com.jpp.mpdomain.MovieSection

/**
 * The initialization parameter used for
 * [MovieListViewModel].
 */
data class MovieListParam(
    val section: MovieSection,
    val screenTitle: String,
    val posterSize: Int,
    val backdropSize: Int
) {
    companion object {
        fun playing(resources: Resources, posterSize: Int, backdropSize: Int) = MovieListParam(
                MovieSection.Playing,
                resources.getString(R.string.main_menu_now_playing),
                posterSize,
                backdropSize
        )

        fun popular(resources: Resources, posterSize: Int, backdropSize: Int) = MovieListParam(
                MovieSection.Popular,
                resources.getString(R.string.main_menu_popular),
                posterSize,
                backdropSize
        )

        fun upcoming(resources: Resources, posterSize: Int, backdropSize: Int) = MovieListParam(
                MovieSection.Upcoming,
                resources.getString(R.string.main_menu_upcoming),
                posterSize,
                backdropSize
        )

        fun topRated(resources: Resources, posterSize: Int, backdropSize: Int) = MovieListParam(
                MovieSection.TopRated,
                resources.getString(R.string.main_menu_top_rated),
                posterSize,
                backdropSize
        )
    }
}
