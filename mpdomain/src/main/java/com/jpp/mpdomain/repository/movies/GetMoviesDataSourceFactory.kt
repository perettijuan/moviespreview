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
     * [backdropSize] indicates the size target of the backdrop images of the Movies. Needed to configure the
     * backdrop URL path.
     * [posterSize] indicates the size target of the backdrop images of the Movies. Needed to configure the
     * poster URL path.
     * [mapper] a mapping function to transform domain objects into another layer objects.
     */
    fun <T> getMoviesForSection(movieSection: MovieSection,
                                backdropSize: Int,
                                posterSize: Int,
                                mapper: (Movie) -> T): LiveData<PagedList<T>>


    /**
     * Retrieves a [LiveData] object that can be used to represent the current state of the
     * operation executed to retrieve the movies pages.
     */
    fun getOperationStateLiveData(): LiveData<OperationState>

}