package com.jpp.mpdata.repository.movies

import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.SupportedLanguage

/**
 * Defines the signature for the database that stores entities related to movies.
 */
interface MoviesDb {
    /**
     * Searches for the [MoviePage] that identified whit the [page] that belongs to the
     * provided [section] and has the proper [language].
     * @return a [MoviePage] is any is stored in the database, null if no data is stored in the database.
     */
    fun getMoviePageForSection(page: Int, section: MovieSection, language: SupportedLanguage): MoviePage?

    /**
     * Stores the provided [MoviePage] in the local database for the provided [section] and [language].
     */
    fun saveMoviePageForSection(moviePage: MoviePage, section: MovieSection, language: SupportedLanguage)

    /**
     * @return a [MovieDetail] for the provided [movieId] if any is found, null
     * any other case.
     */
    fun getMovieDetails(movieId: Double): MovieDetail?

    /**
     * Stores the provided [MovieDetail] in the local database.
     */
    fun saveMovieDetails(movieDetail: MovieDetail)
}