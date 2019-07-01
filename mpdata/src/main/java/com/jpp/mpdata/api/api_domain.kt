package com.jpp.mpdata.api

/*
 * Contains all the body messages that can be used within the API.
 */

data class RequestTokenBody(val request_token: String)

data class FavoriteMediaBody(val media_type: String,
                             val media_id: Double,
                             val favorite: Boolean)

data class FavoriteMediaResponse(val status_code: Double,
                                 val status_message: String)

data class WatchlistMediaBody(val media_type: String,
                              val media_id: Double,
                              val watchlist: Boolean)

data class WatchlistMediaResponse(val status_code: Double,
                                  val status_message: String)