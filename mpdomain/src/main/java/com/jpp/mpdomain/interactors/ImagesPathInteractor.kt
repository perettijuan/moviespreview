package com.jpp.mpdomain.interactors

import com.jpp.mp.common.extensions.transformToInt
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.repository.ConfigurationRepository

/**
 * Interactor commonly used by all module features in the application.
 * By default, when a resource is fetched from the server, the paths that points to
 * the images that the resource can show contains a partial path to a URL. The application
 * needs to fetch a common configuration for the application and configure the path of the
 * original resource with a valid URL formed with the information retrieved in the configuration.
 * This interactor takes care of doing such configuration.
 */
interface ImagesPathInteractor {

    /**
     * Configures the images path of provided [movie] with the target sizes provided.
     * [posterSize] - indicates the target size to use when configuring the poster path of the movie.
     * [backdropSize] - indicates the target size to use when configuring the backdrop path of the movie.
     * [movie] - the movie to configure.
     * @return - a new [Movie] with the same properties that the provided [movie] and the poster path
     * and profile path pointing to a valid URL. If a problem occurs, the paths will contain the same
     * value that the original [movie].
     */
    fun configurePathMovie(posterSize: Int, backdropSize: Int, movie: Movie): Movie

    /**
     * Configure the provided [searchResult] adjusting the images path with the provided [targetImageSize].
     * @return a [SearchResult] with the exact same properties as the provided one, but with the
     * images path pointing to the correct resource.
     */
    fun configureSearchResult(targetImageSize: Int, searchResult: SearchResult): SearchResult

    class Impl(private val configurationRepository: ConfigurationRepository) : ImagesPathInteractor {

        override fun configurePathMovie(posterSize: Int, backdropSize: Int, movie: Movie): Movie {
            return configurationRepository.getAppConfiguration()?.let {
                configureMovieImagesPath(movie, it.images, backdropSize, posterSize)
            } ?: movie
        }

        override fun configureSearchResult(targetImageSize: Int, searchResult: SearchResult): SearchResult {
            return configurationRepository.getAppConfiguration()?.let {
                configureSearchResultImagesPath(searchResult, it.images, targetImageSize)
            } ?: run {
                searchResult
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

        /**
         * Configures the [SearchResult.profile_path], [SearchResult.backdrop_path] and/or
         * [SearchResult.poster_path] properties setting the
         * proper URL based on the provided sizes. It looks for the best possible size based on the
         * supplied ones in the [imagesConfig] to avoid downloading over-sized images.
         * @return a new [SearchResult] object with the same properties as the provided [searchResult],
         * but with the images paths configured.
         */
        private fun configureSearchResultImagesPath(searchResult: SearchResult, imagesConfig: ImagesConfiguration, targetImageSize: Int): SearchResult {
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

        /**
         * Find the best suitable size for the provided [targetSize] and return a valid URL from the combination
         * of [original] and [baseUrl].
         */
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
}