package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository

/**
 * Use case that retrieves a [MoviePage].
 */
class GetMoviePageUseCase(
    private val moviePageRepository: MoviePageRepository,
    private val configurationRepository: ConfigurationRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val languageRepository: LanguageRepository
) {

    suspend fun execute(page: Int, section: MovieSection): Try<MoviePage> {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> Try.Failure(Try.FailureCause.NoConnectivity)
            is Connectivity.Connected ->
                moviePageRepository.getMoviePageForSection(
                    page,
                    section,
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
