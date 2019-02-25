package com.jpp.mpdomain

/***************************************************************************************************
 ********* Contains all the model classes that the domain layer exposes to it's clients.  **********
 *********** The domain layer has its own mapping functions that allows to map one or  *************
 ****************** more domain model classes to one or more data model classes  *******************
 ***************************************************************************************************/

/**
 * Represents the general configuration of the application. Some elements of the API require some
 * knowledge of this configuration data in order to, for instance, crete the URLs that points to
 * the images stored by the API.
 * [images] - contains the configuration base path for all the images that the API supports.
 */
data class AppConfiguration(val images: ImagesConfiguration)

/**
 * Represents the configuration of the images tha the data layer can provide. For instance, a Movie
 * might have more than one image (poster, backdrop, etc.). In order to create the URL that points
 * to that image in the server, you need to use a base URL, size and the path to the file.
 * This class provides the base URL and the sizes that can be used. The path to the file is provided
 * by the object that is being used in the query (the Movie).
 * Example of a URL to an image: https://image.tmdb.org/t/p/w500/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg
 * [base_url] - represents the base URL (https://image.tmdb.org/t/p/)
 * [poster_sizes] - represents the possible sizes for poster images (w500).
 * [profile_sizes] - represents the possible sizes for profile images (w500).
 * [backdrop_sizes] - represents the possible sizes for backdrop images.
 */
data class ImagesConfiguration(val base_url: String,
                               val poster_sizes: List<String>,
                               val profile_sizes: List<String>,
                               val backdrop_sizes: List<String>)

/**
 * Represents a page of [Movie] retrieved from the API server.
 * [page] - the number of the current page.
 * [results] - the list of [Movie] that the page has.
 * [total_pages] - the total number of pages available. Used to verify if there are more
 * pages to retrieve.
 * [total_results] - the total number of results in all pages available.
 */
data class MoviePage(val page: Int,
                     val results: List<Movie>,
                     val total_pages: Int,
                     val total_results: Int)

/**
 * Represents a Movie as it is retrieved from the API server.
 * [id] - the identifier that represents this movie in the model.
 * [title] - the title of the movie to be shown.
 * [original_title] - the original title of the movie, without any translation.
 * [overview] - an synopsis of the movie.
 * [release_date] - the date in which the movie was originally released. The format
 * depends on the language used to fetch the movie.
 * [poster_path] - the path of the poster image. This needs to be used to configure
 * the full URL of the image, using the sizes available in [ImagesConfiguration.poster_sizes].
 * [backdrop_path] - the path of the backdrop image. This needs to be used to configure
 * the full URL of the image, using the sizes available in [ImagesConfiguration.backdrop_sizes].
 * [vote_count] - the total number of votes the movie has in the community.
 * [vote_average] - the average of votes that the movie has in the community.
 * [popularity] - represents how popular the movie is in the community, based in the
 * relative number of votes.
 */
data class Movie(val id: Double,
                 val title: String,
                 val original_title: String,
                 val overview: String,
                 val release_date: String,
                 val original_language: String,
                 val poster_path: String?,
                 val backdrop_path: String?,
                 val vote_count: Double,
                 val vote_average: Float,
                 val popularity: Float)

/**
 * Represents the Genre of a movie.
 * [id] - the identifier of the Genre.
 * [name] - the name to show.
 */
data class MovieGenre(val id: Int,
                      val name: String)

/**
 * Represents the details of a given Movie.
 * [id] - the identifier of the movie.
 * [title] - the title of the movie to be shown.
 * [overview] - an synopsis of the movie.
 * [release_date] - the date in which the movie was originally released. The format
 * depends on the language used to fetch the movie.
 * [poster_path] - the path of the poster image. This needs to be used to configure
 * the full URL of the image, using the sizes available in [ImagesConfiguration.poster_sizes].
 * [genres] - the list of genres that the movie is listed in.
 * [vote_count] - the total number of votes the movie has in the community.
 * [vote_average] - the average of votes that the movie has in the community.
 * [popularity] - represents how popular the movie is in the community, based in the
 * relative number of votes.
 */
data class MovieDetail(val id: Double,
                       val title: String,
                       val overview: String,
                       val release_date: String,
                       val poster_path: String?,
                       val genres: List<MovieGenre>,
                       val vote_count: Double,
                       val vote_average: Float,
                       val popularity: Float)


/**
 * Represents a section of the application in terms of the movies that can be shown.
 */
sealed class MovieSection(val name: String) {
    object Playing : MovieSection("playing")
    object Popular : MovieSection("popular")
    object TopRated : MovieSection("toprated")
    object Upcoming : MovieSection("upcoming")
}


/**
 * Represents a page of results of a searchFirstPage retrieved from the backend.
 * [page] - the page number.
 * [results] - the list of [SearchResult] contained by the page.
 * [total_pages] - the total number of pages that can be retrieved for the current searchPage.
 * [total_results] - the total number of [SearchResult] available for the searchPage.
 */
data class SearchPage(val page: Int,
                      val results: List<SearchResult>,
                      val total_pages: Int,
                      val total_results: Int)

/**
 * Represents an item in a page of searchPage results.
 * [id] - the identifier that represents this movie in the model.
 * [poster_path] - the path of the poster image. This needs to be used to configure
 * the full URL of the image, using the sizes available in [ImagesConfiguration.poster_sizes].
 * [profile_path] - the path of the profile image. This needs to be used to configure
 * the full URL of the image, using the sizes available in [ImagesConfiguration.profile_sizes].
 * [media_type] - represents the type of the result. It might be one of the three: movie, tv, person.
 * [name] - the name of the result. Valid when [media_type] is person.
 * [title] - the title of the result to be shown. Valid when [media_type] is tv or movie.
 * [original_title] - the original title of the movie or tv show,
 * without any translation. Valid when [media_type] is tv or movie.
 * [overview] - an synopsis of the movie or tv show. Valid when [media_type] is tv or movie.
 * [release_date] - the date in which the movie was originally released. The format
 * depends on the language used to fetch the movie.
 * [backdrop_path] - the path of the backdrop image. This needs to be used to configure
 * the full URL of the image, using the sizes available in [ImagesConfiguration.backdrop_sizes].
 * [vote_count] - the total number of votes the movie has in the community.
 * [vote_average] - the average of votes that the movie has in the community.
 * [popularity] - represents how popular the movie is in the community, based in the
 * relative number of votes.
 */
data class SearchResult(val id: Double,
                        val poster_path: String?,
                        val profile_path: String?,
                        val media_type: String,
                        val name: String?,
                        val title: String?,
                        val original_title: String?,
                        val overview: String?,
                        val release_date: String?,
                        val original_language: String?,
                        val backdrop_path: String?,
                        val genre_ids: List<Int>?,
                        val vote_count: Double?,
                        val vote_average: Float?,
                        val popularity: Float?) {

    fun isMovie(): Boolean = media_type == "movie"
    fun isTvShow(): Boolean = media_type == "tv"
    fun isPerson(): Boolean = media_type == "person"
}

/**
 * Represents a person that might be part of a cast or crew.
 * [id] - the identifier that represents this person in the application.
 * [name] - the showing name of the person.
 * [biography] - the bio information of this person.
 * [birthday] - the birthday of the person, if any.
 * [place_of_birth] - the place where the person was born, if the data exists.
 */
data class Person(val id: Double,
                  val name: String,
                  val biography: String,
                  val birthday: String?,
                  val deathday: String?,
                  val place_of_birth: String?)