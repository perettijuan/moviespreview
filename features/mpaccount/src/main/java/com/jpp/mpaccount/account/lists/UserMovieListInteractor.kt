package com.jpp.mpaccount.account.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.extensions.logYourThread
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.*
import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.*
import javax.inject.Inject

//TODO JPP add tests
class UserMovieListInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                                  private val sessionRepository: SessionRepository,
                                                  private val accountRepository: AccountRepository,
                                                  private val moviesRepository: MoviesRepository,
                                                  private val languageRepository: LanguageRepository) {
    sealed class UserMovieListEvent {
        object UserNotLogged : UserMovieListEvent()
        object NotConnectedToNetwork : UserMovieListEvent()
        object UnknownError : UserMovieListEvent()
    }


    private val _userMovieListEvents by lazy { MutableLiveData<UserMovieListEvent>() }

    /**
     * @return a [LiveData] of [UserMovieListEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val userAccountEvents: LiveData<UserMovieListEvent> get() = _userMovieListEvents


    /**
     * Fetches the favorite movies that the user has.
     * The list of Movies will be posted into the [callback].
     * If an error is detected, it will be posted into [userAccountEvents].
     */
    fun fetchFavoriteMovies(page: Int, callback: (List<Movie>) -> Unit) {
        logYourThread()
        fetchFromRepository(callback) { userAccount, session, language ->
            moviesRepository.getFavoriteMoviePage(page, userAccount, session, language)
        }
    }

    /**
     * Fetches the movies that the user has rated.
     * The list of Movies will be posted into the [callback].
     * If an error is detected, it will be posted into [userAccountEvents].
     */
    fun fetchRatedMovies(page: Int, callback: (List<Movie>) -> Unit) {
        logYourThread()
        fetchFromRepository(callback) { userAccount, session, language ->
            moviesRepository.getRatedMoviePage(page, userAccount, session, language)
        }
    }

    /**
     * Fetches the movies that the user has the watchlist.
     * The list of Movies will be posted into the [callback].
     * If an error is detected, it will be posted into [userAccountEvents].
     */
    fun fetchWatchlist(page: Int, callback: (List<Movie>) -> Unit) {
        logYourThread()
        fetchFromRepository(callback) { userAccount, session, language ->
            moviesRepository.getWatchlistMoviePage(page, userAccount, session, language)
        }
    }

    private fun fetchFromRepository(callback: (List<Movie>) -> Unit,
                                    fetch: (UserAccount, Session, SupportedLanguage) -> MoviePage?) {
        withSession { session ->
            withUserAccount(session) { account ->
                when (connectivityRepository.getCurrentConnectivity()) {
                    Connectivity.Disconnected -> _userMovieListEvents.postValue(NotConnectedToNetwork)
                    Connectivity.Connected -> {
                        fetch(account, session, languageRepository.getCurrentAppLanguage())
                                ?.let { callback(it.results) } ?: _userMovieListEvents.postValue(UnknownError)
                    }
                }
            }
        }
    }

    private fun withSession(callback: (Session) -> Unit) {
        when (val session = sessionRepository.getCurrentSession()) {
            null -> _userMovieListEvents.postValue(UserNotLogged)
            else -> callback(session)
        }
    }

    private fun withUserAccount(session: Session, callback: (UserAccount) -> Unit) {
        when (val account = accountRepository.getUserAccount(session)) {
            null -> _userMovieListEvents.postValue(UnknownError)
            else -> callback(account)
        }
    }
}