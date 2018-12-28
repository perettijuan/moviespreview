package com.jpp.moviespreview.domainlayer.repository.movies

import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.MovieSection

interface MoviesApi {
    fun getMoviePageForSection(page: Int, movieSection: MovieSection): MoviePage?
}