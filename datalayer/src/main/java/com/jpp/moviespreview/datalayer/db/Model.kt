package com.jpp.moviespreview.datalayer.db

/***************************************************************************************************
 *********** Contains all the model classes that the specific to the database package.  ************
 *********** All classes defined in the file used by the database package. The classes  ************
 ************       outside this package have no knowledge about this model.          **************
 ***************************************************************************************************/

/**
 * Represents the type of a movie that is stored in the database.
 */
sealed class MovieType {
    object NowPlaying : MovieType()
    object Popular : MovieType()
    object TopRated : MovieType()
    object Upcoming : MovieType()
}