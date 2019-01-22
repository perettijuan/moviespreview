package com.jpp.mpdomain.handlers.configuration

import com.jpp.moviespreview.common.extensions.transformToInt
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.SearchResult

class ConfigurationHandlerImpl : ConfigurationHandler {


    override fun configureMovieImagesPath(movie: Movie, imagesConfig: ImagesConfiguration,
                                          targetBackdropSize: Int, targetPosterSize: Int): Movie {

        return with(movie) {
            copy(
                    poster_path = createUrlForPath(poster_path, imagesConfig.base_url, imagesConfig.poster_sizes, targetPosterSize),
                    backdrop_path = createUrlForPath(backdrop_path, imagesConfig.base_url, imagesConfig.backdrop_sizes, targetBackdropSize)
            )
        }
    }


    override fun configureSearchResult(searchResult: SearchResult, imagesConfig: ImagesConfiguration, targetImageSize: Int): SearchResult {
        return with(searchResult) {
            when (isMovie()) {
                true -> {
                    copy(
                            poster_path = createUrlForPath(poster_path, imagesConfig.base_url, imagesConfig.poster_sizes, targetImageSize),
                            backdrop_path = createUrlForPath(backdrop_path, imagesConfig.base_url, imagesConfig.backdrop_sizes, targetImageSize)
                    )
                }
                false -> copy(profile_path = createUrlForPath(profile_path, imagesConfig.base_url, imagesConfig.profile_sizes, targetImageSize))
            }
        }
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