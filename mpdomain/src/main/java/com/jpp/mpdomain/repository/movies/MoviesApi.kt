package com.jpp.mpdomain.repository.movies

import com.jpp.mpdomain.MoviePage


interface MoviesApi {
    fun getNowPlayingMoviePage(page: Int): MoviePage?
    fun getPopularMoviePage(page: Int): MoviePage?
    fun getTopRatedMoviePage(page: Int): MoviePage?
    fun getUpcomingMoviePage(page: Int): MoviePage?
}