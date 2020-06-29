package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MovieDetailRepository

/**
 * Use case to retrieve a [MovieDetail].
 */
class GetMovieDetailUseCase(
    private val movieDetailRepository: MovieDetailRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val languageRepository: LanguageRepository
) {

    suspend fun execute(movieId: Double): Try<MovieDetail> {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> Try.Failure(Try.FailureCause.NoConnectivity)
            is Connectivity.Connected ->
                movieDetailRepository.getMovieDetails(
                    movieId,
                    languageRepository.getCurrentAppLanguage()
                )?.let {
                    Try.Success(it)
                } ?: Try.Failure(Try.FailureCause.Unknown)
        }
    }
}
