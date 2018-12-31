package com.jpp.mpdomain.repository.movies

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.OperationState

/**
 * Factory class definition to create a [PagedList] of movies.
 */
interface GetMoviesDataSourceFactory {

    /**
     * Called to retrieve the [PagedList] that will hold the list of movies retrieved from the server.
     * [movieSection] indicates the section of interest for the request.
     * [movieImagesConfigurator] a function to configure the [Movie] object when needed. For instance,
     * used to configure the movie images paths.
     * [movieMapper] a mapping function to transform domain objects into another layer objects.
     */
    fun <T> getMoviesForSection(movieSection: MovieSection,
                                movieImagesConfigurator: ((Movie) -> Movie),
                                movieMapper: (Movie) -> T): LiveData<PagedList<T>>


    /**
     * Retrieves a [LiveData] object that can be used to represent the current state of the
     * operation executed to retrieve the movies pages.
     */
    fun getOperationStateLiveData(): LiveData<OperationState>

}