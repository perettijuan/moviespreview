package com.jpp.mpdata.repository.moviedetail

import com.jpp.mpdata.datasources.moviedetail.MovieDetailApi
import com.jpp.mpdata.datasources.moviedetail.MovieDetailDb
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.MovieDetailRepository

class MovieDetailRepositoryImpl(
    private val movieDetailApi: MovieDetailApi,
    private val movieDetailDb: MovieDetailDb
) : MovieDetailRepository {

    override suspend fun getMovieDetails(movieId: Double, language: SupportedLanguage): MovieDetail? {
        return movieDetailDb.getMovieDetails(movieId)
                ?: movieDetailApi.getMovieDetails(movieId, language)?.also { movieDetailDb.saveMovieDetails(it) }
    }

    override suspend fun flushMovieDetailsData() {
        movieDetailDb.flushData()
    }
}
