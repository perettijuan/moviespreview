package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.DBMovie
import com.jpp.mpdata.cache.room.DBMovieDetail
import com.jpp.mpdata.cache.room.DBMovieGenre
import com.jpp.mpdata.cache.room.DBMoviePage
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.MoviePage

class DomainRoomAdapter {

    /**
     * [MoviePage] to [DBMoviePage].
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
     * [Movie] to [DBMovie].
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

    /**
     * [MovieDetail] to [DBMovieDetail].
     */
    fun movieDetail(
        dataMovieDetail: MovieDetail,
        dueDate: Long
    ): DBMovieDetail = DBMovieDetail(
        id = dataMovieDetail.id,
        title = dataMovieDetail.title,
        overview = dataMovieDetail.overview,
        releaseDate = dataMovieDetail.release_date,
        posterPath = dataMovieDetail.poster_path,
        voteCount = dataMovieDetail.vote_count,
        voteAverage = dataMovieDetail.vote_average,
        popularity = dataMovieDetail.popularity,
        dueDate = dueDate
    )

    /**
     * [MovieGenre] to [DBMovieGenre].
     */
    fun genre(
        dataMovieGenre: MovieGenre,
        detailId: Double
    ): DBMovieGenre = DBMovieGenre(
        id = dataMovieGenre.id,
        name = dataMovieGenre.name,
        movieDetailId = detailId
    )
}