package com.jpp.mpdomain.handlers.configuration

import com.jpp.mpdomain.Movie

/**
 * TODO JPP implement me -> when implementing this guy, make sure tha the implementation receives
 * an Executor instance where it executes the configuration work. Take an executor from ServiceLocator.getDiskIOExecutor()
 * in the example.
 */
interface ConfigurationHandler {
    fun configureMovie(movie: Movie,
                       targetBackdropSize: Int,
                       targetPosterSize: Int): Movie
}