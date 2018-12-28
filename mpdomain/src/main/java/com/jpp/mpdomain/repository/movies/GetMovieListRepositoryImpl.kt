package com.jpp.mpdomain.repository.movies

import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import javax.inject.Inject

class GetMovieListRepositoryImpl @Inject constructor(private val dataSourceFactory: GetMoviesDataSourceFactory) : GetMovieListRepository {
    override fun <T> moviePageForSection(section: MovieSection, targetBackdropSize: Int, targetPosterSize: Int, mapper: (Movie) -> T): MovieListing<T> {
        //TODO JPP -> you have to map networkState and retryAllFailed
        val pagedList = dataSourceFactory.getMoviesForSection(
                section,
                targetBackdropSize,
                targetPosterSize,
                mapper
        )
        return MovieListing(
                pagedList = pagedList,
                operationState = dataSourceFactory.getOperationStateLiveData()
        )
    }
}