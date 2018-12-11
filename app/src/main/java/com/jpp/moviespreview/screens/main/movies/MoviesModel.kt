package com.jpp.moviespreview.screens.main.movies

/***************************************************************************************************
 ********* Contains all the model classes that the movies section in the UI layer supports. ********
 ***************************************************************************************************/

/**
 * Represents a section in the movies list.
 * This can be mapped one to one with MovieSection in the domain layer.
 */
sealed class UiMovieSection {
    object Playing : UiMovieSection()
    object Popular : UiMovieSection()
    object TopRated : UiMovieSection()
    object Upcoming : UiMovieSection()
}

/**
 * Represents an item in the list of Movies shown in the initial screen of the application.
 */
data class MovieItem(
        val headerImageUrl: String,
        val title: String,
        val contentImageUrl: String,
        val popularity: String,
        val voteCount: String
)