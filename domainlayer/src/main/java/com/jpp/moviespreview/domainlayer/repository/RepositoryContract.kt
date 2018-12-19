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

    sealed class MoviesRepositoryOutput {
        object Error : MoviesRepositoryOutput()
        data class Success(val page: MoviePage) : MoviesRepositoryOutput()
    }

    fun getNowPlayingMoviePage(page: Int): MoviesRepositoryOutput
    fun getPopularMoviePage(page: Int): MoviesRepositoryOutput
    fun getTopRatedMoviePage(page: Int): MoviesRepositoryOutput
    fun getUpcomingMoviePage(page: Int): MoviesRepositoryOutput
    fun updateNowPlayingMoviePage(moviePage: MoviePage)
    fun updatePopularMoviePage(moviePage: MoviePage)
    fun updateTopRatedMoviePage(moviePage: MoviePage)
    fun updateUpcomingMoviePage(moviePage: MoviePage)
}