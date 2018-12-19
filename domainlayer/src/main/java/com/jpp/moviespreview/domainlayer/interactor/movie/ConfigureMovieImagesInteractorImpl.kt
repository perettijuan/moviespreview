package com.jpp.moviespreview.domainlayer.interactor.movie

import com.jpp.moviespreview.common.extensions.transformToInt
import com.jpp.moviespreview.domainlayer.interactor.ConfigureMovieImagesInteractor
import com.jpp.moviespreview.domainlayer.interactor.MovieImagesParam
import com.jpp.moviespreview.domainlayer.interactor.MovieImagesResult
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository

class ConfigureMovieImagesInteractorImpl(private val configRepository: ConfigurationRepository) : ConfigureMovieImagesInteractor {

    override fun execute(parameter: MovieImagesParam): MovieImagesResult {
        return configRepository.getConfiguration().let { appConfig ->
            when (appConfig) {
                ConfigurationRepository.ConfigurationRepositoryOutput.Error -> MovieImagesResult(parameter.movie)
                is ConfigurationRepository.ConfigurationRepositoryOutput.Success -> {
                    with(parameter.movie) {
                        MovieImagesResult(copy(
                                posterPath = createUrlForPath(posterPath, appConfig.config.baseUrl, appConfig.config.posterSizes, parameter.posterSize),
                                backdropPath = createUrlForPath(backdropPath, appConfig.config.baseUrl, appConfig.config.backdropSizes, parameter.backdropSize)
                        ))
                    }
                }
            }
        }
    }

    private fun createUrlForPath(original: String?, baseUrl: String, sizes: List<String>, targetSize: Int): String? {
        return original?.let {
            StringBuilder()
                    .append(baseUrl)
                    .append(sizes.find { size -> size.transformToInt() ?: 0 >= targetSize }
                            ?: sizes.last())
                    .append(it)
                    .toString()
        }
    }
}