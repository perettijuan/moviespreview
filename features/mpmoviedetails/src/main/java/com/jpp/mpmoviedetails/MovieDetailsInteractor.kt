package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.Connectivity.Connected
import com.jpp.mpdomain.Connectivity.Disconnected
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.*
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieDetailEvent.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interactor to support the movie detail screen. This interactor takes care of accessing the
 * repository layer to perform the inner state updates needed to provide functionality to the
 * view layer.
 */
@Singleton
//TODO JPP Language dependant!
class MovieDetailsInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                                 private val movieDetailRepository: MovieDetailRepository,
                                                 private val languageRepository: LanguageRepository,
                                                 private val sessionRepository: SessionRepository,
                                                 private val accountRepository: AccountRepository,
                                                 private val movieStateRepository: MovieStateRepository,
                                                 private val moviePageRepository: MoviePageRepository) {
    /**
     * Represents the events that this interactor
     * can route to the upper layers.
     */
    sealed class MovieDetailEvent {
        object NotConnectedToNetwork : MovieDetailEvent()
        object UnknownError : MovieDetailEvent()
        data class Success(val data: MovieDetail) : MovieDetailEvent()
    }

    /**
     * Represents the events related to movie states that this interactor
     * can route to the upper layers.
     */
    sealed class MovieStateEvent {
        object NoStateFound : MovieStateEvent()
        object NotConnectedToNetwork : MovieStateEvent()
        object UnknownError : MovieStateEvent()
        object UserNotLogged : MovieStateEvent()
        data class UpdateFavorite(val success: Boolean) : MovieStateEvent()
        data class UpdateWatchlist(val success: Boolean) : MovieStateEvent()
        data class FetchSuccess(val data: MovieState) : MovieStateEvent()
    }

    private val _movieDetailEvents by lazy { MutableLiveData<MovieDetailEvent>() }
    private val _movieStateEvents by lazy { MutableLiveData<MovieStateEvent>() }

    /**
     * @return a [LiveData] of [MovieDetailEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val movieDetailEvents: LiveData<MovieDetailEvent> get() = _movieDetailEvents

    /**
     * @return a [LiveData] of [MovieStateEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val movieStateEvents: LiveData<MovieStateEvent> get() = _movieStateEvents

    /**
     * Fetches the [MovieDetail] that corresponds to the movie identified by [movieId].
     * It will post a new event to [movieDetailEvents] indicating the result of the action.
     */
    fun fetchMovieDetail(movieId: Double) {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Disconnected -> _movieDetailEvents.postValue(NotConnectedToNetwork)
            is Connected -> {
                movieDetailRepository
                        .getMovieDetails(movieId, languageRepository.getCurrentAppLanguage())
                        ?.let { _movieDetailEvents.postValue(Success(it)) }
                        ?: _movieDetailEvents.postValue(UnknownError)
            }
        }
    }

    /**
     * Fetches the [MovieState] that corresponds to the movie identified by [movieId].
     * It will post a new event to [movieStateEvents] indicating the result of the action.
     */
    fun fetchMovieState(movieId: Double) {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Disconnected -> _movieStateEvents.postValue(MovieStateEvent.NotConnectedToNetwork)
            is Connected -> {
                sessionRepository.getCurrentSession()?.let { session ->
                    movieStateRepository.getStateForMovie(movieId, session)?.let { movieState ->
                        _movieStateEvents.postValue(MovieStateEvent.FetchSuccess(movieState))
                    } ?: _movieStateEvents.postValue(MovieStateEvent.UnknownError)
                } ?: _movieStateEvents.postValue(MovieStateEvent.NoStateFound)
            }
        }
    }

    /**
     * Updates the favorite state of the movie identified with [movieId] to [asFavorite].
     */
    fun updateFavoriteMovieState(movieId: Double, asFavorite: Boolean) {
        withAccountData { session, userAccount ->
            movieStateRepository
                    .updateFavoriteMovieState(movieId, asFavorite, userAccount, session)
                    .let { MovieStateEvent.UpdateFavorite(it) }
                    .also { moviePageRepository.flushFavoriteMoviePages() }
                    .let { _movieStateEvents.postValue(it) }
        }
    }

    /**
     * Updates the watchlist state of the movie identified with [movieId] to [inWatchlist].
     */
    fun updateWatchlistMovieState(movieId: Double, inWatchlist: Boolean) {
        withAccountData { session, userAccount ->
            movieStateRepository
                    .updateWatchlistMovieState(movieId, inWatchlist, userAccount, session)
                    .let { MovieStateEvent.UpdateWatchlist(it) }
                    .also { moviePageRepository.flushWatchlistMoviePages() }
                    .let { _movieStateEvents.postValue(it) }
        }
    }

    private fun withAccountData(callback: (Session, UserAccount) -> Unit) {
        when (val session = sessionRepository.getCurrentSession()) {
            null -> _movieStateEvents.postValue(MovieStateEvent.UserNotLogged)
            else -> when (val account = accountRepository.getUserAccount(session)) {
                null -> _movieStateEvents.postValue(MovieStateEvent.UserNotLogged)
                else -> callback(session, account)
            }
        }
    }
}