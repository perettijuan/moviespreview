package com.jpp.mpdomain.repository.details

import com.jpp.mpdomain.MovieDetail

sealed class MovieDetailsRepositoryState {
    object ErrorUnknown : MovieDetailsRepositoryState()
    object ErrorNoConnectivity : MovieDetailsRepositoryState()
    data class Success(val detail: MovieDetail) : MovieDetailsRepositoryState()
}