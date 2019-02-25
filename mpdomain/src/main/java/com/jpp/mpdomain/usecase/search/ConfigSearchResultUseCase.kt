package com.jpp.mpdomain.usecase.search

import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.repository.ConfigurationRepository

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


    class Impl(private val configurationRepository: ConfigurationRepository,
               private val configurationHandler: ConfigurationHandler) : ConfigSearchResultUseCase {

        override fun configure(targetImageSize: Int, searchResult: SearchResult): SearchResult {
            return configurationRepository.getAppConfiguration()?.let {
                configurationHandler.configureSearchResult(searchResult, it.images, targetImageSize)
            } ?: run {
                searchResult
            }
        }
    }
}