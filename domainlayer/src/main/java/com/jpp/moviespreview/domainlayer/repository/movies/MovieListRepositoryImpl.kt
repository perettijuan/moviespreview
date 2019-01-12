package com.jpp.moviespreview.domainlayer.repository.movies

import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.repository.MovieListRepository
import com.jpp.moviespreview.domainlayer.repository.MovieListing

class MovieListRepositoryImpl(private val dataSourceFactory: MoviesPagingDataSourceFactory) : MovieListRepository {
    override fun <T> moviePageOfSection(section: MovieSection, targetBackdropSize: Int, targetPosterSize: Int, mapper: (Movie) -> T): MovieListing<T> {
        //TODO JPP -> you have to map networkState and retryAllFailed
        val pagedList = dataSourceFactory.getMovieList(
                section,
                targetBackdropSize,
                targetPosterSize,
                mapper
        )
        return MovieListing(
                pagedList = pagedList,
                dataSourceLiveData = dataSourceFactory.dataSourceLiveData
        )
    }
}