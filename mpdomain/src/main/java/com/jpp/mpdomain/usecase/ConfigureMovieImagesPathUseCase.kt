package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.repository.ConfigurationRepository

/**
 * Use case that configures the images path (both backdrop and poster) for a specific
 * [Movie].
 */
class ConfigureMovieImagesPathUseCase(
    private val configurationRepository: ConfigurationRepository
) {

    suspend fun execute(movie: Movie): Try<Movie> {
        return configurationRepository.getAppConfiguration()?.let { appConfiguration ->
            Try.Success(movie.configurePaths(appConfiguration.images))
        } ?: Try.Failure(Try.FailureCause.Unknown)
    }
}
