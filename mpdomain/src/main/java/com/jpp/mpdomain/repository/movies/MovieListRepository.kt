package com.jpp.mpdomain.repository.movies

import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection

/**
 * Repository definition to fetch a list of movies for a given section.
 * It hides the fact that we're using the Android Paging Library by wrapping the pieces needed by
 * the library in a [MovieListing] object.
 */
interface MovieListRepository {
    /**
     * Retrieves a [MovieListing] that can be use to show a list of movies retrieved from the server.
     * [section] indicates the section of interest for the request.
     * [targetBackdropSize] indicates the size target of the backdrop images of the Movies. Needed to configure the
     * backdrop URL path.
     * [targetPosterSize] indicates the size target of the backdrop images of the Movies. Needed to configure the
     * poster URL path.
     * [mapper] a mapping function to transform domain objects into another layer objects.
     *
     * @return a [MovieListing] that holds the data related to the movie list.
     */
    fun <T> moviePageForSection(section: MovieSection,
                                targetBackdropSize: Int,
                                targetPosterSize: Int,
                                mapper: (Movie) -> T): MovieListing<T>
}