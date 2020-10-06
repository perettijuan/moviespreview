package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MovieGenreRepository

/**
 * Use case to retrieve the list of all [MovieGenre] available.
 */
class GetAllMovieGenresUseCase(
    private val movieGenreRepository: MovieGenreRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(): Try<List<MovieGenre>> {
        return movieGenreRepository.getMovieGenres()?.let { genreList ->
            Try.Success(genreList)
        } ?: when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> Try.Failure(Try.FailureCause.NoConnectivity)
            else -> Try.Failure(Try.FailureCause.Unknown)
        }
    }
}
