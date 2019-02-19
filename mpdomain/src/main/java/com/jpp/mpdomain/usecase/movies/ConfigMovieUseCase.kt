package com.jpp.mpdomain.usecase.movies

import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.repository.configuration.ConfigurationRepository

/**
 * Defines a use case that configures the images path of a [Movie]. This use case produces a [Movie]
 * that has [Movie.backdrop_path] and [Movie.poster_path] configured with the full URL to the image
 * resource, based on the provided image size.
 */
interface ConfigMovieUseCase {

    /**
     * Configures the provided [movie] adjusting the images path with the provided [posterSize] and
     * [backdropSize].
     * @return a [Movie] with the exact same properties as the provided one, but with the images path
     * pointing to the correct resource.
     */
    fun configure(posterSize: Int, backdropSize: Int, movie: Movie): Movie


    class Impl(private val configurationRepository: ConfigurationRepository,
               private val configurationHandler: ConfigurationHandler) : ConfigMovieUseCase {

        override fun configure(posterSize: Int, backdropSize: Int, movie: Movie): Movie {
            return configurationRepository.getAppConfiguration()?.let {
                configurationHandler.configureMovieImagesPath(movie, it.images, backdropSize, posterSize)
            } ?: run {
                movie
            }
        }
    }
}