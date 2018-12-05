package com.jpp.moviespreview.domainlayer.usecase

import com.jpp.moviespreview.domainlayer.MovieSection

/***************************************************************************************************
 *********** Contains the definition of all the Parameters that Use Cases can receive.  ************
 ***************************************************************************************************/

/**
 * Represents an empty param for use cases where there are no params.
 */
sealed class EmptyParam

/**
 * Parameter to execute [GetMoviePageUseCase].
 *
 * [page] - the number of the page to retrieve.
 * [section] - the section of the movie pages to retrieve.
 */
data class MoviePageParam(val page: Int, val section: MovieSection)