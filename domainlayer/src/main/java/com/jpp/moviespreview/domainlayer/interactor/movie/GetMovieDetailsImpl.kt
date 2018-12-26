package com.jpp.moviespreview.domainlayer.interactor.movie

import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.interactor.GetMovieDetails
import com.jpp.moviespreview.domainlayer.interactor.MovieDetailsParam
import com.jpp.moviespreview.domainlayer.interactor.MovieDetailsResult
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository

class GetMovieDetailsImpl(private val moviesRepository: MoviesRepository,
                          private val connectivityVerifier: ConnectivityVerifier) : GetMovieDetails {

    override fun execute(parameter: MovieDetailsParam): MovieDetailsResult {
        return moviesRepository.getMovieDetail(parameter.movieId).let {
            when (it) {
                is MoviesRepository.MoviesRepositoryOutput.MovieDetailsRetrieved -> MovieDetailsResult.Success(it.detail)
                else -> when (connectivityVerifier.isConnectedToNetwork()) {
                    true -> MovieDetailsResult.ErrorUnknown
                    else -> MovieDetailsResult.ErrorNoConnectivity
                }
            }
        }
    }
}