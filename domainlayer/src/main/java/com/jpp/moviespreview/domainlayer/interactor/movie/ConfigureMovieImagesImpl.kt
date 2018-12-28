package com.jpp.moviespreview.domainlayer.interactor.movie

import com.jpp.moviespreview.common.extensions.transformToInt
import com.jpp.moviespreview.domainlayer.interactor.ConfigureMovieImages
import com.jpp.moviespreview.domainlayer.interactor.MovieImagesParam
import com.jpp.moviespreview.domainlayer.interactor.MovieImagesResult
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository

class ConfigureMovieImagesImpl(private val configRepository: ConfigurationRepository) : ConfigureMovieImages {

    override fun execute(parameter: MovieImagesParam): MovieImagesResult {
        return configRepository.getConfiguration().let { appConfig ->
            when (appConfig) {
                ConfigurationRepository.ConfigurationRepositoryOutput.Error -> MovieImagesResult(parameter.movie)
                is ConfigurationRepository.ConfigurationRepositoryOutput.Success -> {
                    with(parameter.movie) {
                        MovieImagesResult(copy(
                                poster_path = createUrlForPath(poster_path, appConfig.config.images.base_url, appConfig.config.images.poster_sizes, parameter.posterSize),
                                backdrop_path = createUrlForPath(backdrop_path, appConfig.config.images.base_url, appConfig.config.images.backdrop_sizes, parameter.backdropSize)
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