package com.jpp.moviespreview.domainlayer.interactor

import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MoviePage

/***************************************************************************************************
 ************* Contains the definition of all the Results that Use Cases can return.  **************
 ***************************************************************************************************/

/**
 * Represents the results that can be returned by [ConfigureApplication].
 */
sealed class ConfigureApplicationResult {
    object ErrorNoConnectivity : ConfigureApplicationResult()
    object ErrorUnknown : ConfigureApplicationResult()
    object Success : ConfigureApplicationResult()
}

/**
 * Represents the results that can be returned by [GetMoviePage].
 */
sealed class MoviePageResult {
    object ErrorNoConnectivity : MoviePageResult()
    object ErrorUnknown : MoviePageResult()
    data class Success(val moviePage: MoviePage) : MoviePageResult()
}

/**
 * Represents the result of [ConfigureMovieImagesInteractor].
 */
data class MovieImagesResult(val movie: Movie)

/**
 * Represents the result of [GetConfiguredMoviePage].
 */
sealed class ConfiguredMoviePageResult {
    object ErrorNoConnectivity : ConfiguredMoviePageResult()
    object ErrorUnknown : ConfiguredMoviePageResult()
    data class Success(val moviePage: MoviePage) : ConfiguredMoviePageResult()
}