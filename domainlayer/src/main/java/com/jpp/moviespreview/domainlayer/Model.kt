package com.jpp.moviespreview.domainlayer

/***************************************************************************************************
 ********* Contains all the model classes that the domain layer exposes to it's clients.  **********
 *********** The domain layer has its own mapping functions that allows to map one or  *************
 ****************** more domain model classes to one or more data model classes  *******************
 ***************************************************************************************************/


/**
 * Represents the configuration of the images that the application supports.
 */
data class ImagesConfiguration(val baseUrl: String,
                               val posterSizes: List<String>,
                               val profileSizes: List<String>,
                               val backdropSizes: List<String>)

/**
 * Represents a section of the application in terms of the movies that can be shown.
 */
sealed class MovieSection {
    object Playing : MovieSection()
    object Popular : MovieSection()
    object TopRated : MovieSection()
    object Upcoming : MovieSection()
}


/**
 * Represents a page of [Movie] in the domain layer.
 */
data class MoviePage(val pageNumber: Int,
                     val movies: List<Movie>,
                     val totalPages: Int)

/**
 * Represents a Movie in the domain layer.
 */
data class Movie(val id: Double,
                 val title: String,
                 val originalTitle: String,
                 val overview: String,
                 val releaseDate: String,
                 val originalLanguage: String,
                 val posterPath: String?,
                 val backdropPath: String?,
                 val voteCount: Double,
                 val voteAverage: Float,
                 val popularity: Float)


/**
 * Represents the details of a given Movie.
 */
data class MovieDetail(val id: Double,
                       val title: String,
                       val overview: String,
                       val releaseDate: String,
                       val posterPath: String?,
                       val genres: List<Genre>,
                       val voteCount: Double,
                       val voteAverage: Float,
                       val popularity: Float)


/**
 * Represents a movie Genre.
 */
data class Genre(val id: Int,
                 val name: String)