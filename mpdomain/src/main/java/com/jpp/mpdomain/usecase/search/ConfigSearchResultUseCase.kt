package com.jpp.mpdomain.usecase.search

import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.usecase.ConfigureImagePathUseCase

/**
 * Defines a use case that configures the images path of a [SearchResult]. By default the [SearchResult.poster_path],
 * [SearchResult.profile_path] and/or [SearchResult.backdrop_path] properties are initialized to a
 * value that does not contains the full path of the images. This use case takes care of adjusting
 * such properties based on a provided image size.
 */
interface ConfigSearchResultUseCase {
    /**
     * Configure the provided [searchResult] adjusting the images path with the provided [targetImageSize].
     * @return a [SearchResult] with the exact same properties as the provided one, but with the
     * images path pointing to the correct resource.
     */
    fun configure(targetImageSize: Int, searchResult: SearchResult): SearchResult


    class Impl(private val configurationRepository: ConfigurationRepository) : ConfigSearchResultUseCase, ConfigureImagePathUseCase() {

        override fun configure(targetImageSize: Int, searchResult: SearchResult): SearchResult {
            return configurationRepository.getAppConfiguration()?.let {
                configureSearchResult(searchResult, it.images, targetImageSize)
            } ?: run {
                searchResult
            }
        }

        /**
         * Configures the [SearchResult.profile_path], [SearchResult.backdrop_path] and/or
         * [SearchResult.poster_path] properties setting the
         * proper URL based on the provided sizes. It looks for the best possible size based on the
         * supplied ones in the [imagesConfig] to avoid downloading over-sized images.
         * @return a new [SearchResult] object with the same properties as the provided [searchResult],
         * but with the images paths configured.
         */
        private fun configureSearchResult(searchResult: SearchResult, imagesConfig: ImagesConfiguration, targetImageSize: Int): SearchResult {
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
    }
}