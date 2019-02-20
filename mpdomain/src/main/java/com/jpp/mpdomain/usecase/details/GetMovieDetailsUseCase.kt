package com.jpp.mpdomain.usecase.details

import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.repository.MoviesRepository

/**
 * Defines a UseCase that retrieves the details of a particular movie.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected, retrieve the details of the movie identified with movieId.
 * If not connected, return an error that indicates such state.
 */
interface GetMovieDetailsUseCase {
    /**
     * Retrieves the details of a particular movie identified with [movieId].
     * @return
     *  - [GetMovieDetailsUseCaseResult.Success] when details are found for the particular movie.
     *  - [GetMovieDetailsUseCaseResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [GetMovieDetailsUseCaseResult.ErrorUnknown] when an error occur while fetching the details.
     */
    fun getDetailsForMovie(movieId: Double) : GetMovieDetailsUseCaseResult


    class Impl(private val moviesRepository: MoviesRepository,
               private val connectivityHandler: ConnectivityHandler) : GetMovieDetailsUseCase {

        override fun getDetailsForMovie(movieId: Double): GetMovieDetailsUseCaseResult {
            return when(connectivityHandler.isConnectedToNetwork()) {
                false -> GetMovieDetailsUseCaseResult.ErrorNoConnectivity
                true -> moviesRepository.getMovieDetails(movieId)?.let {
                    GetMovieDetailsUseCaseResult.Success(it)
                } ?: run {
                    GetMovieDetailsUseCaseResult.ErrorUnknown
                }
            }
        }
    }
}