package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.repository.*

/**
 * Use case to retrieve a watch-listed [MoviePage].
 */
class GetWatchListMoviePage(
    private val moviePageRepository: MoviePageRepository,
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository,
    private val configurationRepository: ConfigurationRepository,
    private val languageRepository: LanguageRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(page: Int): Try<MoviePage> {
        if (connectivityRepository.getCurrentConnectivity() is Connectivity.Disconnected) {
            return Try.Failure(Try.FailureCause.NoConnectivity)
        }

        val currentSession = sessionRepository.getCurrentSession()
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        val userAccount = accountRepository.getUserAccount(currentSession)
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        return moviePageRepository.getWatchlistMoviePage(
            page = page,
            session = currentSession,
            userAccount = userAccount,
            language = languageRepository.getCurrentAppLanguage()
        )?.let { favPage ->
            Try.Success(favPage.copy(results = favPage.results.configureMovieImages()))
        } ?: Try.Failure(Try.FailureCause.Unknown)
    }

    private fun List<Movie>.configureMovieImages(): List<Movie> {
        val imagesConfig = configurationRepository.getAppConfiguration()?.images ?: return this
        return toMutableList().map { movie -> movie.configurePaths(imagesConfig) }
    }
}