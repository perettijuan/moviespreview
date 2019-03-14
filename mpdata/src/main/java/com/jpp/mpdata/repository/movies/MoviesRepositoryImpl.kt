package com.jpp.mpdata.repository.movies

import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.MoviesRepository

class MoviesRepositoryImpl(private val moviesApi: MoviesApi,
                           private val moviesDb: MoviesDb) : MoviesRepository {

    override fun getMoviePageForSection(page: Int, section: MovieSection, language: SupportedLanguage): MoviePage? {
        return moviesDb.getMoviePageForSection(page, section, language) ?: run {
            getFromApi(page, section, language)?.also {
                moviesDb.saveMoviePageForSection(it, section, language)
            }
        }
    }

    override fun getMovieDetails(movieId: Double): MovieDetail? {
        return moviesDb.getMovieDetails(movieId) ?: run {
            moviesApi.getMovieDetails(movieId)?.also {
                moviesDb.saveMovieDetails(it)
            }
        }
    }

    private fun getFromApi(page: Int, section: MovieSection, language: SupportedLanguage): MoviePage? = with(moviesApi) {
        when (section) {
            MovieSection.Playing -> getNowPlayingMoviePage(page, language)
            MovieSection.TopRated -> getTopRatedMoviePage(page, language)
            MovieSection.Popular -> getPopularMoviePage(page, language)
            MovieSection.Upcoming -> getUpcomingMoviePage(page ,language)
        }
    }
}