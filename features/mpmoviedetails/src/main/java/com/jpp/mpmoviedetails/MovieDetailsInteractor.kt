package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.Connectivity.Connected
import com.jpp.mpdomain.Connectivity.Disconnected
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MovieDetailRepository
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieDetailEvent.*
import javax.inject.Inject

//TODO JPP add tests
class MovieDetailsInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                                 private val movieDetailRepository: MovieDetailRepository,
                                                 private val languageRepository: LanguageRepository) {
    /**
     * Represents the events that this interactor
     * can route to the upper layers.
     */
    sealed class MovieDetailEvent {
        object NotConnectedToNetwork : MovieDetailEvent()
        object UnknownError : MovieDetailEvent()
        data class Success(val data: MovieDetail) : MovieDetailEvent()
    }

    private val _movieDetailEvents by lazy { MutableLiveData<MovieDetailEvent>() }

    /**
     * @return a [LiveData] of [MovieDetailEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val movieDetailEvents: LiveData<MovieDetailEvent> get() = _movieDetailEvents

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
}