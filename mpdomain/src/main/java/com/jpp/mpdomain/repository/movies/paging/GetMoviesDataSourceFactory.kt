package com.jpp.mpdomain.repository.movies.paging

import androidx.paging.DataSource
import com.jpp.mpdomain.Movie

/**
 * A [DataSource.Factory] implementation, part of the paging library. It only takes care
 * of creating the proper [DataSource] when needed.
 */
class GetMoviesDataSourceFactory(private val fetchItems: (Int, (List<Movie>, Int) -> Unit) -> Unit)
    : DataSource.Factory<Int, Movie>() {

    lateinit var datasourceInstance: GetMoviesDataSource

    /**
     * From [DataSource.Factory#create()]
     */
    override fun create(): DataSource<Int, Movie> {
        datasourceInstance = GetMoviesDataSource(fetchItems)
        return datasourceInstance
    }


    fun retryLast() {
        datasourceInstance.retryLastCall()
    }
}