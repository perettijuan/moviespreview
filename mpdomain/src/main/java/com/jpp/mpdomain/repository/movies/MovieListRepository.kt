package com.jpp.mpdomain.repository.movies

import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection

interface MovieListRepository {
    /**
     * Retrieves a [MovieListing] that can be use to show a list of movies retrieved from the server.
     * [section] indicates the section of interest for the request.
     * [targetBackdropSize] indicates the size target of the backdrop images of the Movies. Needed to configure the
     * backdrop URL path.
     * [targetPosterSize] indicates the size target of the backdrop images of the Movies. Needed to configure the
     * poster URL path.
     * [mapper] a mapping function to transform domain objects into another layer objects.
     */
    fun <T> moviePageForSection(section: MovieSection,
                                targetBackdropSize: Int,
                                targetPosterSize: Int,
                                mapper: (Movie) -> T): MovieListing<T>
}