package com.jpp.mpdata.api

data class RequestTokenBody(val request_token: String)
data class FavoriteMediaBody(val media_type: String,
                             val media_id: Double,
                             val favorite: Boolean)
data class FavoriteMediaResponse(val status_code: Double,
                                 val status_message: String)
