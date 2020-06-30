package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.AccountMovieType
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.SessionRepository

/**
 * Use case to retrieve a [MoviePage] of a particular [AccountMovieType].
 */
class GetUserAccountMoviePageUseCase(
    private val moviePageRepository: MoviePageRepository,
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository,
    private val configurationRepository: ConfigurationRepository,
    private val languageRepository: LanguageRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(page: Int, type: AccountMovieType): Try<MoviePage> {
        if (connectivityRepository.getCurrentConnectivity() is Connectivity.Disconnected) {
            return Try.Failure(Try.FailureCause.NoConnectivity)
        }

        val currentSession = sessionRepository.getCurrentSession()
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        val userAccount = accountRepository.getUserAccount(currentSession)
            ?: return Try.Failure(Try.FailureCause.UserNotLogged)

        return getMoviePage(
            page,
            type,
            currentSession,
            userAccount,
            languageRepository.getCurrentAppLanguage()
        )?.let { favPage ->
            Try.Success(
                favPage.copy(
                    results = favPage.results.configureMovieImages(configurationRepository.getAppConfiguration())
                )
            )
        } ?: Try.Failure(Try.FailureCause.Unknown)
    }

    private suspend fun getMoviePage(
        page: Int,
        type: AccountMovieType,
        session: Session,
        userAccount: UserAccount,
        language: SupportedLanguage
    ): MoviePage? {
        return when (type) {
            is AccountMovieType.Favorite -> moviePageRepository.getFavoriteMoviePage(
                page = page,
                session = session,
                userAccount = userAccount,
                language = language
            )
            is AccountMovieType.Rated -> moviePageRepository.getRatedMoviePage(
                page = page,
                session = session,
                userAccount = userAccount,
                language = language
            )
            is AccountMovieType.Watchlist -> moviePageRepository.getWatchlistMoviePage(
                page = page,
                session = session,
                userAccount = userAccount,
                language = language
            )
        }
    }
}
