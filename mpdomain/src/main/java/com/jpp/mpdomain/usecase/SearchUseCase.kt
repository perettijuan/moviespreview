package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.SearchRepository

/**
 * Use case to perform a search.
 */
class SearchUseCase(
    private val searchRepository: SearchRepository,
    private val configurationRepository: ConfigurationRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val languageRepository: LanguageRepository
) {

    suspend fun execute(query: String, page: Int): Try<SearchPage> {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> Try.Failure(Try.FailureCause.NoConnectivity)
            is Connectivity.Connected -> searchRepository.searchPage(
                query,
                page,
                languageRepository.getCurrentAppLanguage()
            )?.let { searchPage ->
                Try.Success(searchPage.copy(results = searchPage.results.map { result ->
                    configureSearchResultPaths(result)
                }))
            } ?: Try.Failure(Try.FailureCause.Unknown)
        }
    }

    private suspend fun configureSearchResultPaths(searchResult: SearchResult): SearchResult {
        return configurationRepository.getAppConfiguration()?.let { appConfiguration ->
            searchResult.configureImagesPath(appConfiguration.images)
        } ?: searchResult
    }

    private fun SearchResult.configureImagesPath(imagesConfig: ImagesConfiguration): SearchResult {
        return if (isMovie()) {
            copy(
                poster_path = poster_path.createUrlForPath(
                    imagesConfig.base_url,
                    imagesConfig.poster_sizes.last()
                ),
                backdrop_path = backdrop_path.createUrlForPath(
                    imagesConfig.base_url,
                    imagesConfig.backdrop_sizes.last()
                )
            )
        } else {
            copy(
                profile_path = profile_path.createUrlForPath(
                    imagesConfig.base_url,
                    imagesConfig.profile_sizes.last()
                )
            )
        }
    }
}
