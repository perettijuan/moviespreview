package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.Connectivity.Connected
import com.jpp.mpdomain.Connectivity.Disconnected
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interactor to support the movie detail screen. This interactor takes care of accessing the
 * repository layer to perform the inner state updates needed to provide functionality to the
 * view layer.
 */
@Singleton
@Deprecated("Use the respective use cases instead")
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

    private val _rateMovieEvents = MutableLiveData<RateMovieEvent>()
    val rateMovieEvents: LiveData<RateMovieEvent> get() = _rateMovieEvents


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


    private fun withAccountData(callback: (Session, UserAccount) -> Unit) {

    }
}
