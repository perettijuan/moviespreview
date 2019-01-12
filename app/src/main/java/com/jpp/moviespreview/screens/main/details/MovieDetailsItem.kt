package com.jpp.moviespreview.screens.main.details

data class MovieDetailsItem(
        val title: String,
        val overview: String,
        val releaseDate: String,
        val voteCount: Double,
        val voteAverage: Float,
        val popularity: Float
)