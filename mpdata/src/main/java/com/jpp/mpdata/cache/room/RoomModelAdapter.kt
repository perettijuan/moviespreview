package com.jpp.mpdata.cache.room

import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import javax.inject.Inject

class RoomModelAdapter @Inject constructor() {

    /**
     * Adapts the provided [DBMoviePage] to a data layer [MoviePage] with the same data.
     */
    fun adaptDBMoviePageToDataMoviePage(dbMoviePage: DBMoviePage, movies: List<DBMovie>): MoviePage = with(dbMoviePage) {
        MoviePage(
                page = page,
                results = movies.map { adaptDBMovieToDataMovie(it) },
                total_pages = totalPages,
                total_results = totalResults
        )
    }

    /**
     * Adapts the provided [DBMovie] to a data layer [Movie] with the same data.
     */
    private fun adaptDBMovieToDataMovie(dbMovie: DBMovie): Movie = with(dbMovie) {
        Movie(
                id = id,
                title = title,
                original_title = originalTile,
                overview = overview,
                release_date = releaseDate,
                original_language = originalLanguage,
                poster_path = posterPath,
                backdrop_path = backdropPath,
                vote_count = voteCount,
                vote_average = voteAverage,
                popularity = popularity
        )
    }

    /**
     * Adapts the provided [MoviePage] to a [DBMoviePage] with the same data.
     */
    fun adaptDataMoviePageToDBMoviePage(dataMoviePage: MoviePage): DBMoviePage = with(dataMoviePage) {
        DBMoviePage(
                page = page,
                totalPages = total_pages,
                totalResults = total_results
        )
    }

    /**
     * Adapts the provided [Movie] to a [DBMovie] with the respective data.
     */
    fun adaptDataMovieToDBMovie(dataMovie: Movie, pageId: Int): DBMovie = with(dataMovie) {
        DBMovie(
                id = id,
                title = title,
                originalTile = original_title,
                overview = overview,
                releaseDate = release_date,
                originalLanguage = original_language,
                posterPath = poster_path,
                backdropPath = backdrop_path,
                voteCount = vote_count,
                voteAverage = vote_average,
                popularity = popularity,
                pageId = pageId
        )
    }
}