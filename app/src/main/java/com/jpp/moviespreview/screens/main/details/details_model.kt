package com.jpp.moviespreview.screens.main.details

import androidx.annotation.DrawableRes
import com.jpp.moviespreview.R

data class MovieDetailsItem(
        val title: String,
        val overview: String,
        val releaseDate: String,
        val voteCount: Double,
        val voteAverage: Float,
        val popularity: Float,
        val genres: List<MovieGenreItem>
)


sealed class MovieGenreItem(@DrawableRes val icon: Int) {
    object Action : MovieGenreItem(R.drawable.ic_action)
    object Adventure : MovieGenreItem(R.drawable.ic_adventure)
    object Animation : MovieGenreItem(R.drawable.ic_animation)
    object Comedy : MovieGenreItem(R.drawable.ic_comedy)
    object Crime : MovieGenreItem(R.drawable.ic_crime)
    object Documentary : MovieGenreItem(R.drawable.ic_documentary)
    object Drama : MovieGenreItem(R.drawable.ic_drama)
    object Family : MovieGenreItem(R.drawable.ic_family)
    object Fantasy : MovieGenreItem(R.drawable.ic_fantasy)
    object History : MovieGenreItem(R.drawable.ic_history)
    object Horror : MovieGenreItem(R.drawable.ic_horror)
    object Music : MovieGenreItem(R.drawable.ic_music)
    object Mystery : MovieGenreItem(R.drawable.ic_mystery)
    object SciFi : MovieGenreItem(R.drawable.ic_science_ficcion)
    object TvMovie : MovieGenreItem(R.drawable.ic_tv_movie)
    object Thriller : MovieGenreItem(R.drawable.ic_thriller)
    object War : MovieGenreItem(R.drawable.ic_war)
    object Western : MovieGenreItem(R.drawable.ic_western)
    object Generic : MovieGenreItem(R.drawable.ic_generic)
}

