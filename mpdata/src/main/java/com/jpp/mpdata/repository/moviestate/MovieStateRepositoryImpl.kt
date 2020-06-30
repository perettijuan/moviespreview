package com.jpp.mpdata.repository.moviestate

import com.jpp.mpdata.datasources.moviestate.MovieStateApi
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.MovieStateRepository

class MovieStateRepositoryImpl(private val movieStateApi: MovieStateApi) : MovieStateRepository {

    override suspend fun getStateForMovie(movieId: Double, session: Session): MovieState? {
        /*
         * TODO JPP for the moment, we don't store this state in the local storage
         * BUT it is a great candidate to store it and try to use the WorkManager
         * to sync the state with the API
         */
        return movieStateApi.getMovieAccountState(movieId, session)
    }

    override suspend fun updateFavoriteMovieState(movieId: Double, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean {
        return movieStateApi
                .updateFavoriteMovieState(movieId, asFavorite, userAccount, session)?.let { true }
                ?: false
    }

    override suspend fun updateWatchlistMovieState(movieId: Double, inWatchList: Boolean, userAccount: UserAccount, session: Session): Boolean {
        return movieStateApi
                .updateWatchlistMovieState(movieId, inWatchList, userAccount, session)?.let { true }
                ?: false
    }

    override suspend fun rateMovie(movieId: Double, rating: Float, userAccount: UserAccount, session: Session): Boolean {
        return movieStateApi
                .rateMovie(movieId, rating, userAccount, session)
                ?: false
    }

    override suspend fun deleteMovieRate(movieId: Double, session: Session): Boolean {
        return movieStateApi
                .deleteMovieRating(movieId, session)
                ?: false
    }
}
