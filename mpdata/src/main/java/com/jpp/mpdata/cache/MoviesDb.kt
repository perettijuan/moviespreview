package com.jpp.mpdata.cache

import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection

interface MoviesDb {
    fun getMoviePageForSection(page: Int, section: MovieSection): MoviePage?
    fun saveMoviePageForSection(moviePage: MoviePage, section: MovieSection)
}