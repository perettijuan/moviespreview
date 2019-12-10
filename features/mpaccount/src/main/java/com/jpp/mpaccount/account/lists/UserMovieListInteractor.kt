package com.jpp.mpaccount.account.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.NotConnectedToNetwork
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.UnknownError
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.UserChangedLanguage
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.UserNotLogged
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.SessionRepository
import javax.inject.Inject

/**
 * Interactor to support the list of movies that the user has in the account (either favorites,
 * rated or watchlist).
 * Since this interactor is used with the paging library, it provides two ways to communicate the
 * responses obtained from the data layer:
 *  - One way, is a [LiveData] object that publishes status events obtained in error
 *    scenarios ([UserMovieListEvent]).
 *  - The other way is using a callback. This is defined this way because of the limitation
 *    in the paging library, where a callback needs to be provided - instead of using
 *    a reactive approach.
 */
class UserMovieListInteractor @Inject constructor(
    private val connectivityRepository: ConnectivityRepository,
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository,
    private val moviePageRepository: MoviePageRepository,
    private val languageRepository: LanguageRepository
) {
    sealed class UserMovieListEvent {
        object UserChangedLanguage : UserMovieListEvent()
        object UserNotLogged : UserMovieListEvent()
        object NotConnectedToNetwork : UserMovieListEvent()
        object UnknownError : UserMovieListEvent()
    }

    private val _userMovieListEvents = MediatorLiveData<UserMovieListEvent>()

    init {
        _userMovieListEvents.addSource(languageRepository.updates()) { _userMovieListEvents.postValue(UserChangedLanguage) }
    }

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
        // for debug reasons logYourThread()
        fetchFromRepository(callback) { userAccount, session, language ->
            moviePageRepository.getFavoriteMoviePage(page, userAccount, session, language)
        }
    }

    /**
     * Fetches the movies that the user has rated.
     * The list of Movies will be posted into the [callback].
     * If an error is detected, it will be posted into [userAccountEvents].
     */
    fun fetchRatedMovies(page: Int, callback: (List<Movie>) -> Unit) {
        // for debug reasons logYourThread()
        fetchFromRepository(callback) { userAccount, session, language ->
            moviePageRepository.getRatedMoviePage(page, userAccount, session, language)
        }
    }

    /**
     * Fetches the movies that the user has the watchlist.
     * The list of Movies will be posted into the [callback].
     * If an error is detected, it will be posted into [userAccountEvents].
     */
    fun fetchWatchlist(page: Int, callback: (List<Movie>) -> Unit) {
        // for debug reasons logYourThread()
        fetchFromRepository(callback) { userAccount, session, language ->
            moviePageRepository.getWatchlistMoviePage(page, userAccount, session, language)
        }
    }

    fun refreshUserMoviesData() {
        with(moviePageRepository) {
            flushFavoriteMoviePages()
            flushRatedMoviePages()
            flushWatchlistMoviePages()
        }
    }

    /**
     * Fetches the data from the repository by verifying the application's inner state (user
     * logged in and with an account obtained) before executing the [fetch].
     * The results of this fetching process are notified to the [callback] on a success
     * scenario. On an error scenario, the state is published via the [LiveData] object
     * obtained in [userAccountEvents].
     */
    private fun fetchFromRepository(
        callback: (List<Movie>) -> Unit,
        fetch: (UserAccount, Session, SupportedLanguage) -> MoviePage?
    ) {
        withSession { session ->
            withUserAccount(session) { account ->
                when (connectivityRepository.getCurrentConnectivity()) {
                    Connectivity.Disconnected -> _userMovieListEvents.postValue(NotConnectedToNetwork)
                    Connectivity.Connected -> {
                        fetch(account, session, languageRepository.getCurrentAppLanguage())
                                ?.let { callback(it.results) }
                                ?: _userMovieListEvents.postValue(UnknownError)
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
