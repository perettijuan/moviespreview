package com.jpp.mpdomain.handlers.configuration

import com.jpp.moviespreview.common.extensions.transformToInt
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie

class ConfigurationHandlerImpl : ConfigurationHandler {

    override fun configureMovie(movie: Movie,
                                imagesConfig: ImagesConfiguration,
                                targetBackdropSize: Int,
                                targetPosterSize: Int): Movie =
        with(movie) {
            copy(
                    poster_path = createUrlForPath(poster_path, imagesConfig.base_url, imagesConfig.poster_sizes, targetPosterSize),
                    backdrop_path = createUrlForPath(backdrop_path, imagesConfig.base_url, imagesConfig.backdrop_sizes, targetBackdropSize)
            )
        }


    private fun createUrlForPath(original: String?, baseUrl: String, sizes: List<String>, targetSize: Int): String? {
        return original?.let {
            StringBuilder()
                    .append(baseUrl)
                    .append(sizes.find { size -> size.transformToInt() ?: 0 >= targetSize }
                            ?: sizes.last())
                    .append(it)
                    .toString()
        }
    }

}