package com.jpp.mpdomain.repository

import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.Session

/**
 * Repository definition to handle [MovieState].
 */
interface MovieStateRepository {
    /**
     * @return the [MovieState] that indicates the state of the provided [movieId]
     * for the [session]. If an error is detected, null is returned.
     */
    fun getStateForMovie(movieId: Double, session: Session): MovieState?
}