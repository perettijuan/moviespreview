package com.jpp.mpdomain.usecase.credits

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.CreditsRepository

/**
 * Defines a UseCase that retrieves the credits of a given movie.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected, retrieve the credits for the movie identified with the provided id.
 * If not connected, return an error that indicates such state.
 */
interface GetCreditsUseCase {
    /**
     * Retrieves the credits of a movie identified with [movieId].
     * @return
     *  - [GetCreditsResult.Success] when the credits are found.
     *  - [GetCreditsResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [GetCreditsResult.ErrorUnknown] when an error occur while fetching the credits.
     */
    fun getCreditsForMovie(movieId: Double): GetCreditsResult


    class Impl(private val creditsRepository: CreditsRepository,
               private val connectivityRepository: ConnectivityRepository) : GetCreditsUseCase {
        override fun getCreditsForMovie(movieId: Double): GetCreditsResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Connectivity.Disconnected -> GetCreditsResult.ErrorNoConnectivity
                Connectivity.Connected -> creditsRepository.getCreditsForMovie(movieId)?.let {
                    GetCreditsResult.Success(it)
                } ?: run {
                    GetCreditsResult.ErrorUnknown
                }
            }
        }
    }
}