package com.jpp.mpdata.repository.movies

import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.MoviesRepository

class MoviesRepositoryImpl(private val moviesApi: MoviesApi,
                           private val moviesDb: MoviesDb) : MoviesRepository {

    override fun getMoviePageForSection(page: Int, section: MovieSection): MoviePage? {
        return moviesDb.getMoviePageForSection(page, section) ?: run {
            getFromApi(page, section)?.also {
                moviesDb.saveMoviePageForSection(it, section)
            }
        }
    }

    private fun getFromApi(page: Int, section: MovieSection): MoviePage? = with(moviesApi) {
        when (section) {
            MovieSection.Playing -> getNowPlayingMoviePage(page)
            MovieSection.TopRated -> getTopRatedMoviePage(page)
            MovieSection.Popular -> getPopularMoviePage(page)
            MovieSection.Upcoming -> getUpcomingMoviePage(page)
        }
    }
}