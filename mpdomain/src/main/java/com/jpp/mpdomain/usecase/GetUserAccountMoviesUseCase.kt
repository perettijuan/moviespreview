package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.AccountMovieType
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Use case to retrieve a page of movies that are related to each
 * [AccountMovieType] of a user's account.
 */
class GetUserAccountMoviesUseCase(
    private val moviePageRepository: MoviePageRepository,
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository,
    private val configurationRepository: ConfigurationRepository,
    private val languageRepository: LanguageRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(page: Int): Try<Map<AccountMovieType, MoviePage>> {
        if (connectivityRepository.getCurrentConnectivity() is Connectivity.Disconnected) {
            return Try.Failure(Try.FailureCause.NoConnectivity)
        }

        val currentSession = sessionRepository.getCurrentSession()
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        val userAccount = accountRepository.getUserAccount(currentSession)
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        val language = languageRepository.getCurrentAppLanguage()

        val appConfiguration = configurationRepository.getAppConfiguration()

        val favoritesPage = moviePageRepository.getFavoriteMoviePage(
            page = page,
            session = currentSession,
            userAccount = userAccount,
            language = language
        )?.let { favs ->
            favs.copy(results = favs.results.configureMovieImages(appConfiguration))
        }

        val inWatchListPage = moviePageRepository.getWatchlistMoviePage(
            page = page,
            session = currentSession,
            userAccount = userAccount,
            language = language
        )?.let { watch ->
            watch.copy(results = watch.results.configureMovieImages(appConfiguration))
        }

        val ratedPage = moviePageRepository.getRatedMoviePage(
            page = page,
            session = currentSession,
            userAccount = userAccount,
            language = language
        )?.let { rated ->
            rated.copy(results = rated.results.configureMovieImages(appConfiguration))
        }

        if (favoritesPage == null || inWatchListPage == null || ratedPage == null) {
            return Try.Failure(Try.FailureCause.Unknown)
        }

        return mapOf(
            AccountMovieType.Favorite to favoritesPage,
            AccountMovieType.Watchlist to inWatchListPage,
            AccountMovieType.Rated to ratedPage
        ).let { map -> Try.Success(map) }
    }
}
