package com.jpp.mpdata.datasources.moviestate

import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.Session

interface MovieStateApi {
    /**
     * @return the [MovieState] for the provided [movieId] and the [session]. If
     * an error is detected, returns null.
     */
    fun getMovieAccountState(movieId: Double, session: Session): MovieState?
}