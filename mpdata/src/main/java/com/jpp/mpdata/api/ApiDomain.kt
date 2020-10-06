package com.jpp.mpdata.api

import com.jpp.mpdomain.MovieGenre

/*
 * This file contains the entities that are needed to execute certain API requests.
 * Since not all API requests can be mapped one to one with domain objects, we need
 * this middle layer to wrap/unwrap the domain classes.
 */

/**
 * Body used to request an access token to the API.
 */
internal data class RequestTokenBody(val request_token: String)

/**
 * Body used to update the favorite state of a given resource in the API.
 */
internal data class FavoriteMediaBody(
    val media_type: String,
    val media_id: Double,
    val favorite: Boolean
)

/**
 * Body used to rate a movie.
 */
internal data class RateMovieBody(val value: Float)

/**
 * Response obtained when a movie is rated.
 */
internal data class RateMovieResponse(
    val status_code: Double,
    val status_message: String
)

/**
 * Response obtained when the favorite state of a given resource is updated
 * in the API.
 */
internal data class FavoriteMediaResponse(
    val status_code: Double,
    val status_message: String
)

/**
 * Body used to add/remove a resource to the watchlist in the API.
 */
internal data class WatchlistMediaBody(
    val media_type: String,
    val media_id: Double,
    val watchlist: Boolean
)

/**
 * Response obtained when a resource is added to / removed from the watchlist
 * in the API.
 */
internal data class WatchlistMediaResponse(
    val status_code: Double,
    val status_message: String
)

/**
 * Response obtained when retrieving the movie genres from the server.
 */
internal data class MovieGenresResponse(
    val genres: List<MovieGenre>
)