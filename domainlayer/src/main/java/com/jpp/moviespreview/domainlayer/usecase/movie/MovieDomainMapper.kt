package com.jpp.moviespreview.domainlayer.usecase.movie

import com.jpp.moviespreview.datalayer.MoviePage as DataMoviePage
import com.jpp.moviespreview.datalayer.Movie as DataMovie
import com.jpp.moviespreview.domainlayer.MoviePage as DomainMoviePage
import com.jpp.moviespreview.domainlayer.Movie as DomainMovie

/**
 * In order to keep the layers independent, each layer exposes its own
 * model. This class takes care of mapping all data classes related to the movies topic
 * into domain classes related to the same movies topic.
 * This mapper knows the data layer types and the domain layer types.
 */
class MovieDomainMapper {

    /**
     * Maps a [DataMoviePage] into a [DomainMoviePage].
     */
    fun mapDataPageToDomainPage(dataMoviePage: DataMoviePage): DomainMoviePage = with(dataMoviePage) {
        DomainMoviePage(
                page,
                results.map { mapDataMovieToDomainMovie(it) },
                total_pages
        )
    }

    /**
     * Maps a [DataMovie] into a [DomainMovie].
     */
    private fun mapDataMovieToDomainMovie(dataMovie: DataMovie) = with(dataMovie) {
        DomainMovie(id,
                title,
                original_title,
                overview,
                release_date,
                original_language,
                poster_path,
                backdrop_path,
                vote_count,
                vote_average,
                popularity)
    }
}