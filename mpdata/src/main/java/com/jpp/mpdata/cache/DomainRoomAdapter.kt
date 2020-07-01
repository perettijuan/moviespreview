package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.DBMovie
import com.jpp.mpdata.cache.room.DBMoviePage
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage

class DomainRoomAdapter {

    /**
     * Adapts the provided [MoviePage] to a [DBMoviePage] with the same data.
     */
    fun moviePage(
        dataMoviePage: MoviePage,
        sectionName: String,
        dueDate: Long
    ): DBMoviePage = DBMoviePage(
        page = dataMoviePage.page,
        totalPages = dataMoviePage.total_pages,
        totalResults = dataMoviePage.total_results,
        section = sectionName,
        dueDate = dueDate
    )

    /**
     * Adapts the provided [Movie] to a [DBMovie] with the respective data.
     */
    fun movie(dataMovie: Movie, pageId: Long): DBMovie = DBMovie(
        movieId = dataMovie.id,
        title = dataMovie.title,
        originalTile = dataMovie.original_title,
        overview = dataMovie.overview,
        releaseDate = dataMovie.release_date,
        originalLanguage = dataMovie.original_language,
        posterPath = dataMovie.poster_path,
        backdropPath = dataMovie.backdrop_path,
        voteCount = dataMovie.vote_count,
        voteAverage = dataMovie.vote_average,
        popularity = dataMovie.popularity,
        pageId = pageId
    )
}