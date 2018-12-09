package com.jpp.moviespreview.screens.main.movies.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePageInteractor
import com.jpp.moviespreview.screens.main.movies.UiMovieSection
import java.lang.IllegalStateException


/**
 * Factory class to create the DataSource that will provide the pages to show in the movies section
 * of the application.
 * An instance of this class is injected into the ViewModels to hook the callbacks to the UI.
 */
class MoviesPagingDataSourceFactory(private val moviePageInteractor: GetMoviePageInteractor) : DataSource.Factory<Int, Movie>() {


    val dataSourceLiveData by lazy { MutableLiveData<MoviesPagingDataSource>() }
    var currentSection: UiMovieSection? = null


    override fun create(): DataSource<Int, Movie> {
        currentSection?.let {
            val dataSource = MoviesPagingDataSource(moviePageInteractor, it)
            dataSourceLiveData.postValue(dataSource)
            return dataSource
        }
        throw IllegalStateException("You need to provide a section to initialize the data source")
    }
}