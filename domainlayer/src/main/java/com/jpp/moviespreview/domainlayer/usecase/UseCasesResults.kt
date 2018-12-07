package com.jpp.moviespreview.domainlayer.usecase

import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MoviePage

/***************************************************************************************************
 ************* Contains the definition of all the Results that Use Cases can return.  **************
 ***************************************************************************************************/

/**
 * Represents the results that can be returned by [ConfigureApplicationUseCase].
 */
sealed class ConfigureApplicationResult {
    object ErrorNoConnectivity : ConfigureApplicationResult()
    object ErrorUnknown : ConfigureApplicationResult()
    object Success : ConfigureApplicationResult()
}

/**
 * Represents the results that can be returned by [GetMoviePageUseCase].
 */
sealed class MoviePageResult {
    object ErrorNoConnectivity : MoviePageResult()
    object ErrorUnknown : MoviePageResult()
    data class Success(val moviePage: MoviePage) : MoviePageResult()
    data class BadParams(val message: String) : MoviePageResult()
}

/**
 * Represents the result of [ConfigureMovieImagesUseCase].
 */
data class MovieImagesResult(val movie: Movie)