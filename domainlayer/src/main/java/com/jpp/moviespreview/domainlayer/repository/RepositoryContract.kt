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
    fun getNowPlayingMoviePage(page: Int): MoviePage?
    fun getPopularMoviePage(page: Int): MoviePage?
    fun getTopRatedMoviePage(page: Int): MoviePage?
    fun getUpcomingMoviePage(page: Int): MoviePage?
    fun updateNowPlayingMoviePage(moviePage: MoviePage)
    fun updatePopularMoviePage(moviePage: MoviePage)
    fun updateTopRatedMoviePage(moviePage: MoviePage)
    fun updateUpcomingMoviePage(moviePage: MoviePage)
}