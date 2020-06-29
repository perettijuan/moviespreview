package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie

/**
 * Extension to create a URL from a String.
 */
internal fun String?.createUrlForPath(baseUrl: String, size: String): String? {
    return this.let {
        StringBuilder()
            .append(baseUrl)
            .append(size)
            .append(it)
            .toString()
    }
}

/**
 * Extension to configure the images paths of a [Movie].
 */
internal fun Movie.configurePaths(imagesConfig: ImagesConfiguration): Movie {
    return copy(
        poster_path = poster_path.createUrlForPath(
            imagesConfig.base_url,
            imagesConfig.poster_sizes.last()
        ),
        backdrop_path = backdrop_path.createUrlForPath(
            imagesConfig.base_url,
            imagesConfig.poster_sizes.last()
        )
    )
}

/**
 * Extension to configure the paths of a list of movies.
 */
internal fun List<Movie>.configureMovieImages(appConfiguration: AppConfiguration?): List<Movie> {
    val imagesConfig = appConfiguration?.images ?: return this
    return toMutableList().map { movie -> movie.configurePaths(imagesConfig) }
}
