package com.jpp.mpdomain.usecase.movies

import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.usecase.ConfigureImagePathUseCase

/**
 * Defines a use case that configures the images path of a [Movie]. This use case produces a [Movie]
 * that has [Movie.backdrop_path] and [Movie.poster_path] configured with the full URL to the image
 * resource, based on the provided image size.
 */
interface ConfigMovieUseCase {

    /**
     * Represents the result of the movie configuration use case.
     */
    data class ConfigMovieResult(val movie: Movie)

    /**
     * Configures the provided [movie] adjusting the images path with the provided [posterSize] and
     * [backdropSize].
     * @return a [Movie] with the exact same properties as the provided one, but with the images path
     * pointing to the correct resource.
     */
    fun configure(posterSize: Int, backdropSize: Int, movie: Movie): ConfigMovieResult


    class Impl(private val configurationRepository: ConfigurationRepository) : ConfigMovieUseCase, ConfigureImagePathUseCase() {

        override fun configure(posterSize: Int, backdropSize: Int, movie: Movie): ConfigMovieResult {
            return configurationRepository.getAppConfiguration()?.let {
                ConfigMovieResult(configureMovieImagesPath(movie, it.images, backdropSize, posterSize))
            } ?: run {
                ConfigMovieResult(movie)
            }
        }

        /**
         * Configures the [Movie.poster_path] and [Movie.backdrop_path] properties setting the
         * proper URL based on the provided sizes. It looks for the best possible size based on the
         * supplied ones in the [imagesConfig] to avoid downloading over-sized images.
         * @return a new [Movie] object with the same attributes as the original one, but with
         * the images paths configured.
         */
        private fun configureMovieImagesPath(movie: Movie, imagesConfig: ImagesConfiguration,
                                             targetBackdropSize: Int, targetPosterSize: Int): Movie {
            return movie.copy(
                    poster_path = createUrlForPath(movie.poster_path, imagesConfig.base_url, imagesConfig.poster_sizes, targetPosterSize),
                    backdrop_path = createUrlForPath(movie.backdrop_path, imagesConfig.base_url, imagesConfig.backdrop_sizes, targetBackdropSize)
            )
        }
    }
}