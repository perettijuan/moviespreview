package com.jpp.mpaccount.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.account.UserAccountInteractor.UserAccountEvent.*
import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.*
import javax.inject.Inject

/**
 * Interactor to support the user account screen. This interactor takes care of accessing the
 * repository layer to perform the inner state updates needed to provide functionality to the
 * user account screen.
 */
class UserAccountInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                                private val sessionRepository: SessionRepository,
                                                private val accountRepository: AccountRepository,
                                                private val moviesRepository: MoviesRepository,
                                                private val languageRepository: LanguageRepository) {

    /**
     * Represents the events that this interactor can route to the upper layers.
     */
    sealed class UserAccountEvent {
        object UserNotLogged : UserAccountEvent()
        object NotConnectedToNetwork : UserAccountEvent()
        object UnknownError : UserAccountEvent()
        data class Success(val data: UserAccount,
                           val favoriteMovies: UserMoviesState,
                           val ratedMovies: UserMoviesState,
                           val watchList: UserMoviesState)
            : UserAccountEvent()
    }

    sealed class UserMoviesState {
        object UnknownError : UserMoviesState()
        data class Success(val data: MoviePage) : UserMoviesState()
    }

    private val _userAccountEvents by lazy { MutableLiveData<UserAccountEvent>() }

    /**
     * @return a [LiveData] of [UserAccountEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val userAccountEvents: LiveData<UserAccountEvent> get() = _userAccountEvents

    /**
     * Fetches the user account data to be shown in the screen.
     */
    fun fetchUserAccountData() {
        val session = sessionRepository.getCurrentSession()
        when (session) {
            null -> UserNotLogged
            else -> getUserAccount(session, languageRepository.getCurrentAppLanguage())
        }.let {
            _userAccountEvents.postValue(it)
        }
    }

    private fun getUserAccount(session: Session, language: SupportedLanguage): UserAccountEvent {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> NotConnectedToNetwork
            is Connectivity.Connected -> accountRepository.getUserAccount(session)?.let {
                Success(
                        data = it,
                        favoriteMovies = getFavoriteMovies(session, it, language),
                        ratedMovies = getRatedMovies(session, it, language),
                        watchList = getWatchlist(session, it, language)
                )
            } ?: UnknownError
        }
    }

    private fun getFavoriteMovies(session: Session, userAccount: UserAccount, language: SupportedLanguage): UserMoviesState {
        return moviesRepository.getFavoriteMoviePage(
                page = 1,
                userAccount = userAccount,
                session = session,
                language = language
        )?.let { favMoviePage ->
            UserMoviesState.Success(favMoviePage)
        } ?: UserMoviesState.UnknownError
    }

    private fun getRatedMovies(session: Session, userAccount: UserAccount, language: SupportedLanguage): UserMoviesState {
        return moviesRepository.getRatedMoviePage(
                page = 1,
                userAccount = userAccount,
                session = session,
                language = language
        )?.let { favMoviePage ->
            UserMoviesState.Success(favMoviePage)
        } ?: UserMoviesState.UnknownError
    }

    private fun getWatchlist(session: Session, userAccount: UserAccount, language: SupportedLanguage): UserMoviesState {
        return moviesRepository.getWatchlistMoviePage(
                page = 1,
                userAccount = userAccount,
                session = session,
                language = language
        )?.let { favMoviePage ->
            UserMoviesState.Success(favMoviePage)
        } ?: UserMoviesState.UnknownError
    }
}