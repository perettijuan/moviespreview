package com.jpp.moviespreview.domainlayer.repository

import com.jpp.moviespreview.domainlayer.ImagesConfiguration
import com.jpp.moviespreview.domainlayer.MoviePage


/**
 * Repository definition to retrieve the [ImagesConfiguration] from
 * the data module.
 */
interface ConfigurationRepository {
    fun getConfiguration(): ImagesConfiguration?
    fun updateAppConfiguration(imagesConfiguration: ImagesConfiguration)
}

/**
 * Repository definition to handle all the data storage related to [Movie]s.
 */
interface MoviesRepository {

    sealed class MoviesRepositoryResult {
        object Error : MoviesRepositoryResult()
        data class Success(val page: MoviePage) : MoviesRepositoryResult()
    }

    fun getNowPlayingMoviePage(page: Int): MoviesRepositoryResult
    fun getPopularMoviePage(page: Int): MoviesRepositoryResult
    fun getTopRatedMoviePage(page: Int): MoviesRepositoryResult
    fun getUpcomingMoviePage(page: Int): MoviesRepositoryResult
    fun updateNowPlayingMoviePage(moviePage: MoviePage)
    fun updatePopularMoviePage(moviePage: MoviePage)
    fun updateTopRatedMoviePage(moviePage: MoviePage)
    fun updateUpcomingMoviePage(moviePage: MoviePage)
}