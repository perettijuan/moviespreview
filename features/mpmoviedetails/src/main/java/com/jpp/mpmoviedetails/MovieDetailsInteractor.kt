package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.Connectivity.Connected
import com.jpp.mpdomain.Connectivity.Disconnected
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MovieDetailRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.MovieStateRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieDetailEvent.AppLanguageChanged
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieDetailEvent.NotConnectedToNetwork
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieDetailEvent.Success
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieDetailEvent.UnknownError
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interactor to support the movie detail screen. This interactor takes care of accessing the
 * repository layer to perform the inner state updates needed to provide functionality to the
 * view layer.
 */
@Singleton
class MovieDetailsInteractor @Inject constructor(
    private val connectivityRepository: ConnectivityRepository,
    private val movieDetailRepository: MovieDetailRepository,
    private val languageRepository: LanguageRepository,
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository,
    private val movieStateRepository: MovieStateRepository,
    private val moviePageRepository: MoviePageRepository
) {
    /**
     * Represents the events that this interactor
     * can route to the upper layers.
     */
    sealed class MovieDetailEvent {
        object AppLanguageChanged : MovieDetailEvent()
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

    /**
     * Represents the events related to movie ratings that this interactor
     * can route to the upper layers.
     */
    sealed class RateMovieEvent {
        object UnknownError : RateMovieEvent()
        object UserNotLogged : RateMovieEvent()
        object NotConnectedToNetwork : RateMovieEvent()
        data class FetchSuccess(val data: MovieState) : RateMovieEvent()
        data class RateMovie(val success: Boolean) : RateMovieEvent()
        data class RatingDeleted(val success: Boolean) : RateMovieEvent()
    }

    private val _movieDetailEvents = MediatorLiveData<MovieDetailEvent>()
    val movieDetailEvents: LiveData<MovieDetailEvent> get() = _movieDetailEvents

    private val _movieStateEvents = MutableLiveData<MovieStateEvent>()
    val movieStateEvents: LiveData<MovieStateEvent> get() = _movieStateEvents

    private val _rateMovieEvents = MutableLiveData<RateMovieEvent>()
    val rateMovieEvents: LiveData<RateMovieEvent> get() = _rateMovieEvents

    init {
        _movieDetailEvents.addSource(languageRepository.updates()) {
            _movieDetailEvents.postValue(AppLanguageChanged)
        }
    }

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
        whenConnected {
            sessionRepository.getCurrentSession()?.let { session ->
                movieStateRepository.getStateForMovie(movieId, session)?.let { movieState ->
                    _movieStateEvents.postValue(MovieStateEvent.FetchSuccess(movieState))
                } ?: _movieStateEvents.postValue(MovieStateEvent.UnknownError)
            } ?: _movieStateEvents.postValue(MovieStateEvent.NoStateFound)
        }
    }

    /**
     * Updates the favorite state of the movie identified with [movieId] to [asFavorite].
     */
    fun updateFavoriteMovieState(movieId: Double, asFavorite: Boolean) {
        whenConnected {
            withAccountData { session, userAccount ->
                movieStateRepository
                        .updateFavoriteMovieState(movieId, asFavorite, userAccount, session)
                        .let { MovieStateEvent.UpdateFavorite(it) }
                        .also { moviePageRepository.flushFavoriteMoviePages() }
                        .let { _movieStateEvents.postValue(it) }
            }
        }
    }

    /**
     * Updates the watchlist state of the movie identified with [movieId] to [inWatchlist].
     */
    fun updateWatchlistMovieState(movieId: Double, inWatchlist: Boolean) {
        whenConnected {
            withAccountData { session, userAccount ->
                movieStateRepository
                        .updateWatchlistMovieState(movieId, inWatchlist, userAccount, session)
                        .let { MovieStateEvent.UpdateWatchlist(it) }
                        .also { moviePageRepository.flushWatchlistMoviePages() }
                        .let { _movieStateEvents.postValue(it) }
            }
        }
    }

    /**
     * Fetches the [MovieState] that corresponds to the movie identified by [movieId].
     * It will post a new event to [rateMovieEvents] indicating the result of the action.
     */
    fun fetchMovieRating(movieId: Double) {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Disconnected -> _rateMovieEvents.postValue(RateMovieEvent.NotConnectedToNetwork)
            is Connected -> sessionRepository.getCurrentSession()?.let { session ->
                movieStateRepository.getStateForMovie(movieId, session)?.let { movieState ->
                    _rateMovieEvents.postValue(RateMovieEvent.FetchSuccess(movieState))
                } ?: _rateMovieEvents.postValue(RateMovieEvent.UnknownError)
            } ?: _rateMovieEvents.postValue(RateMovieEvent.UserNotLogged)
        }
    }

    /**
     * Rates the movie identified by [movieId] by adding the proper [rating].
     */
    fun rateMovie(movieId: Double, rating: Float) {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Disconnected -> _rateMovieEvents.postValue(RateMovieEvent.NotConnectedToNetwork)
            is Connected -> withAccountData { session, userAccount ->
                movieStateRepository
                        .rateMovie(movieId, rating, userAccount, session)
                        .let { RateMovieEvent.RateMovie(it) }
                        .also { moviePageRepository.flushRatedMoviePages() }
                        .let { _rateMovieEvents.postValue(it) }
            }
        }
    }

    /**
     * Deletes the rating that the user has previously set in the movie identified
     * by [movieId].
     */
    fun deleteMovieRating(movieId: Double) {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Disconnected -> _rateMovieEvents.postValue(RateMovieEvent.NotConnectedToNetwork)
            is Connected -> withAccountData { session, _ ->
                movieStateRepository
                        .deleteMovieRate(movieId, session)
                        .let { RateMovieEvent.RatingDeleted(it) }
                        .also { moviePageRepository.flushRatedMoviePages() }
                        .let { _rateMovieEvents.postValue(it) }
            }
        }
    }

    /**
     * Flushes out any movie details stored data.
     */
    fun flushMovieDetailsData() {
        movieDetailRepository.flushMovieDetailsData()
    }

    private fun whenConnected(action: () -> Unit) {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Disconnected -> _movieStateEvents.postValue(MovieStateEvent.NotConnectedToNetwork)
            is Connected -> action()
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
