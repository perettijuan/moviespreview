package com.jpp.moviespreview.domainlayer.repository.movies

import androidx.paging.DataSource
import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.MovieSection

interface MoviesDb {
    fun insertMoviePage(movieSection: MovieSection, page: MoviePage)
    fun moviesBySection(movieSection: MovieSection): DataSource.Factory<Int, MoviePage>
}