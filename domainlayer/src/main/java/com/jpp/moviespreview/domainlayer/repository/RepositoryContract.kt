package com.jpp.moviespreview.domainlayer.repository

import com.jpp.moviespreview.domainlayer.AppConfiguration
import com.jpp.moviespreview.domainlayer.ImagesConfiguration
import com.jpp.moviespreview.domainlayer.MovieDetail
import com.jpp.moviespreview.domainlayer.MoviePage


/**
 * Repository definition to retrieve the [ImagesConfiguration] from
 * the data module.
 */
interface ConfigurationRepository {

    sealed class ConfigurationRepositoryOutput {
        object Error : ConfigurationRepositoryOutput()
        data class Success(val config: AppConfiguration) : ConfigurationRepositoryOutput()
    }


    fun getConfiguration(): ConfigurationRepositoryOutput
    fun updateAppConfiguration(appConfiguration: AppConfiguration)
}

/**
 * Repository definition to handle all the data storage related to [Movie]s.
 */
interface MoviesRepository {

    sealed class MoviesRepositoryOutput {
        object Error : MoviesRepositoryOutput()
        data class MoviePageRetrieved(val page: MoviePage) : MoviesRepositoryOutput()
        data class MovieDetailsRetrieved(val detail: MovieDetail) : MoviesRepositoryOutput()
    }

    fun getNowPlayingMoviePage(page: Int): MoviesRepositoryOutput
    fun getPopularMoviePage(page: Int): MoviesRepositoryOutput
    fun getTopRatedMoviePage(page: Int): MoviesRepositoryOutput
    fun getUpcomingMoviePage(page: Int): MoviesRepositoryOutput
    fun getMovieDetail(movieId: Double): MoviesRepositoryOutput
    fun updateNowPlayingMoviePage(moviePage: MoviePage)
    fun updatePopularMoviePage(moviePage: MoviePage)
    fun updateTopRatedMoviePage(moviePage: MoviePage)
    fun updateUpcomingMoviePage(moviePage: MoviePage)
    fun updateMovieDetail(movieDetail: MovieDetail)
}