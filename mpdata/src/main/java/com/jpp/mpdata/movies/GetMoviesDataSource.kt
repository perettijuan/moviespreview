package com.jpp.mpdata.movies

import androidx.paging.PageKeyedDataSource
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import java.util.concurrent.Executor

class GetMoviesDataSource(private val currentSection: MovieSection,
                          private val backdropSize: Int,
                          private val posterSize: Int,
                          private val retryExecutor: Executor) : PageKeyedDataSource<Int, Movie>() {


    /*
     * This method is responsible to load the data initially
     * when app screen is launched for the first time.
     * We are fetching the first page data from the api
     * and passing it via the callback method to the UI.
     */
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*
     * This method it is responsible for the subsequent call to load the data page wise.
     * This method is executed in the background thread
     * We are fetching the next page data from the api
     * and passing it via the callback method to the UI.
     * The "params.key" variable will have the updated value.
     */
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        //no-op
    }
}