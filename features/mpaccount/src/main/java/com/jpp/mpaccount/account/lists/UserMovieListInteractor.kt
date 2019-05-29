package com.jpp.mpaccount.account.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.UnknownError
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.UserNotLogged
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.NotConnectedToNetwork
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.*
import javax.inject.Inject


class UserMovieListInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                                  private val sessionRepository: SessionRepository,
                                                  private val accountRepository: AccountRepository,
                                                  private val moviesRepository: MoviesRepository,
                                                  private val languageRepository: LanguageRepository) {


    //TODO JPP can we avoid using an executor for the PagedList?

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


    fun fetchFavoriteMovies(page: Int, callback: (List<Movie>) -> Unit) {
        withSession { session ->
            withUserAccount(session) { account ->
                moviesRepository.getFavoriteMoviePage(page, account, session, languageRepository.getCurrentAppLanguage())
                        ?.let { callback(it.results) }
                        ?: when (connectivityRepository.getCurrentConnectivity()) {
                            Connectivity.Connected -> _userMovieListEvents.postValue(UnknownError)
                            Connectivity.Disconnected -> _userMovieListEvents.postValue(NotConnectedToNetwork)
                        }
            }
        }
    }

    /**
     * Fetches the user account data to be shown in the screen.
     */
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