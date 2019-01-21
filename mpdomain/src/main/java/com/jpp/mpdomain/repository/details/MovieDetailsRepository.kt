package com.jpp.mpdomain.repository.details

/**
 * Repository definition to fetch a movie detail.
 */
interface MovieDetailsRepository {

    /**
     * @return a [MovieDetailsRepositoryState] that represents the state of the repository
     * while it is retrieving the movie detail for the movie identified by the [movieId].
     */
    fun getDetail(movieId: Double): MovieDetailsRepositoryState
}