package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository

/**
 * Use case to retrieve a discovered [MoviePage].
 * A discovered MoviePage is a MoviePage that is retrieved from the API
 * that allows movies discovery.
 */
class GetDiscoveredMoviePageUseCase(
    private val moviePageRepository: MoviePageRepository,
    private val configurationRepository: ConfigurationRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val languageRepository: LanguageRepository
) {

    suspend fun execute(page: Int): Try<MoviePage> {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> Try.Failure(Try.FailureCause.NoConnectivity)
            is Connectivity.Connected ->
                moviePageRepository.discover(
                    page,
                    languageRepository.getCurrentAppLanguage()
                )?.let { moviePage ->
                    Try.Success(
                        moviePage.copy(
                            results = moviePage.results.configureMovieImages(
                                configurationRepository.getAppConfiguration()
                            )
                        )
                    )
                } ?: Try.Failure(Try.FailureCause.Unknown)
        }
    }
}