package com.jpp.moviespreview.domainlayer.interactor.movie

import com.jpp.moviespreview.common.extensions.transformToInt
import com.jpp.moviespreview.datalayer.repository.ConfigurationRepository
import com.jpp.moviespreview.domainlayer.interactor.ConfigureMovieImagesInteractor
import com.jpp.moviespreview.domainlayer.interactor.MovieImagesParam
import com.jpp.moviespreview.domainlayer.interactor.MovieImagesResult

class ConfigureMovieImagesInteractorImpl(private val configRepository: ConfigurationRepository) : ConfigureMovieImagesInteractor {

    override fun execute(parameter: MovieImagesParam?): MovieImagesResult {
        return parameter?.let {
            configRepository.getConfiguration()?.let { appConfig ->
                with(parameter.movie) {
                    MovieImagesResult(copy(
                            posterPath = createUrlForPath(posterPath, appConfig.images.base_url, appConfig.images.poster_sizes, parameter.posterSize),
                            backdropPath = createUrlForPath(backdropPath, appConfig.images.base_url, appConfig.images.backdrop_sizes, parameter.backdropSize)
                    ))
                }
            } ?: MovieImagesResult(parameter.movie)
        } ?: throw IllegalArgumentException("The provided parameter can not be null")
    }

    private fun createUrlForPath(original: String?, baseUrl: String, sizes: List<String>, targetSize: Int): String? {
        return original?.let {
            StringBuilder()
                    .append(baseUrl)
                    .append(sizes.find { size -> size.transformToInt() ?: 0 >= targetSize } ?: sizes.last())
                    .append(it)
                    .toString()
        }
    }
}