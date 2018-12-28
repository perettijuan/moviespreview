package com.jpp.moviespreview.datalayer.cache


import com.jpp.moviespreview.domainlayer.AppConfiguration
import com.jpp.moviespreview.domainlayer.MovieDetail
import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.MovieSection

interface MPDataBase {
    fun getStoredAppConfiguration(): AppConfiguration?
    fun updateAppConfiguration(appConfiguration: AppConfiguration)
    fun isCurrentMovieTypeStored(movieType: MovieSection): Boolean
    fun updateCurrentMovieTypeStored(movieType: MovieSection)
    fun getMoviePage(page: Int): MoviePage?
    fun updateMoviePage(page: MoviePage)
    fun clearMoviePagesStored()
    fun getMovieDetail(movieDetailId: Double): MovieDetail?
    fun cleanMovieDetail(movieDetailId: Double)
    fun saveMovieDetail(movieDetail: MovieDetail)
}
