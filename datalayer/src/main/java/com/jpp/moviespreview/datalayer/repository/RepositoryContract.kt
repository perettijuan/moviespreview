package com.jpp.moviespreview.datalayer.repository

import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.MoviePage

/**
 * Repository definition to retrieve the [AppConfiguration] from
 * the data module.
 */
interface ConfigurationRepository {
    fun getConfiguration(): AppConfiguration?
    fun updateAppConfiguration(appConfiguration: AppConfiguration)
}

/**
 * Repository definition to handle all the data storage related to [Movie]s
 */
interface MoviesRepository {
    fun getNowPlayingMoviePage(page: Int): MoviePage?
}