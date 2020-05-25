package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.repository.ConfigurationRepository

/**
 * Use case that configures the images path (both backdrop and poster) for a specific
 * [Movie].
 */
class ConfigureMovieImagesPathUseCase(
        private val configurationRepository: ConfigurationRepository
) {

    fun execute(movie: Movie): Try<Movie> {
        return configurationRepository.getAppConfiguration()?.let { appConfiguration ->
            Try.Success(movie.configurePaths(appConfiguration.images))
        } ?: Try.Failure(Try.FailureCause.Unknown)
    }

    private fun Movie.configurePaths(imagesConfig: ImagesConfiguration): Movie {
        return copy(
                poster_path = poster_path.createUrlForPath(imagesConfig.base_url, imagesConfig.poster_sizes.last()),
                backdrop_path = poster_path.createUrlForPath(imagesConfig.base_url, imagesConfig.poster_sizes.last())
        )
    }

    private fun String?.createUrlForPath(baseUrl: String, size: String): String? {
        return this.let {
            StringBuilder()
                    .append(baseUrl)
                    .append(size)
                    .append(it)
                    .toString()
        }
    }
}