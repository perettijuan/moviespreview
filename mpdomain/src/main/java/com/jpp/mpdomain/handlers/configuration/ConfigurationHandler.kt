package com.jpp.mpdomain.handlers.configuration

import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie

/**
 * Handles all the tasks that are specific to app configuration. For instance: setting the proper
 * URL path to the movies images.
 */
interface ConfigurationHandler {

    /**
     * Configures the [Movie.poster_path] and [Movie.backdrop_path] properties setting the
     * proper URL based on the provided sizes. It looks for the best possible size based on the
     * supplied ones in the [imagesConfig] to avoid downloading over-sized images.
     * @return a new [Movie] object with the same attributes as the original one, but with
     * the images paths configured.
     */
    fun configureMovieImagesPath(movie: Movie,
                                 imagesConfig: ImagesConfiguration,
                                 targetBackdropSize: Int,
                                 targetPosterSize: Int): Movie
}