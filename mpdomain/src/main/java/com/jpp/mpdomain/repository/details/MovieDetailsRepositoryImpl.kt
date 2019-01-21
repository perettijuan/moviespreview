package com.jpp.mpdomain.repository.details

import com.jpp.mpdomain.handlers.ConnectivityHandler

class MovieDetailsRepositoryImpl(private val detailsApi: MovieDetailsApi,
                                 private val detailsDb: MovieDetailsDb,
                                 private val connectivityHandler: ConnectivityHandler) : MovieDetailsRepository {


    override fun getDetail(movieId: Double): MovieDetailsRepositoryState {
        return when (connectivityHandler.isConnectedToNetwork()) {
            false -> MovieDetailsRepositoryState.ErrorNoConnectivity
            true -> {
                getMovieDetails(movieId)?.let {
                    MovieDetailsRepositoryState.Success(it)
                } ?: run {
                    MovieDetailsRepositoryState.ErrorUnknown
                }
            }
        }
    }


    private fun getMovieDetails(movieId: Double) =
        detailsDb.getMovieDetails(movieId) ?: run {
            detailsApi.getMovieDetails(movieId)?.also {
                detailsDb.saveMovieDetails(it)
            }
        }
}