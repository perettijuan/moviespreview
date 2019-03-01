package com.jpp.mpdomain.usecase.details

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.ConnectivityRepository
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
     *  - [GetMovieDetailsResult.Success] when details are found for the particular movie.
     *  - [GetMovieDetailsResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [GetMovieDetailsResult.ErrorUnknown] when an error occur while fetching the details.
     */
    fun getDetailsForMovie(movieId: Double) : GetMovieDetailsResult


    class Impl(private val moviesRepository: MoviesRepository,
               private val connectivityRepository: ConnectivityRepository) : GetMovieDetailsUseCase {

        override fun getDetailsForMovie(movieId: Double): GetMovieDetailsResult {
            return when(connectivityRepository.getCurrentConnectivity()) {
                Connectivity.Disconnected -> GetMovieDetailsResult.ErrorNoConnectivity
                Connectivity.Connected -> moviesRepository.getMovieDetails(movieId)?.let {
                    GetMovieDetailsResult.Success(it)
                } ?: run {
                    GetMovieDetailsResult.ErrorUnknown
                }
            }
        }
    }
}