package com.jpp.mpmoviedetails

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Represents an item in the list of genres that a movie can belong to.
 */
internal enum class MovieGenreItem(@DrawableRes val icon: Int, @StringRes val nameRes: Int) {
    Action(R.drawable.ic_action, R.string.action_genre),
    Adventure(R.drawable.ic_adventure, R.string.adventure_genre),
    Animation(R.drawable.ic_animation, R.string.animation_genre),
    Comedy(R.drawable.ic_comedy, R.string.comedy_genre),
    Crime(R.drawable.ic_crime, R.string.crime_genre),
    Documentary(R.drawable.ic_documentary, R.string.documentary_genre),
    Drama(R.drawable.ic_drama, R.string.drama_genre),
    Family(R.drawable.ic_family, R.string.familiy_genre),
    Fantasy(R.drawable.ic_fantasy, R.string.fantasy_genre),
    History(R.drawable.ic_history, R.string.history_genre),
    Horror(R.drawable.ic_horror, R.string.horror_genre),
    Music(R.drawable.ic_music, R.string.music_genre),
    Mystery(R.drawable.ic_mystery, R.string.mystery_genre),
    SciFi(R.drawable.ic_science_ficcion, R.string.sci_fi_genre),
    TvMovie(R.drawable.ic_tv_movie, R.string.tv_movie_genre),
    Thriller(R.drawable.ic_thriller, R.string.thriller_genre),
    War(R.drawable.ic_war, R.string.war_genre),
    Western(R.drawable.ic_western, R.string.western_genre),
    Generic(R.drawable.ic_generic, R.string.generic_genre)
}
