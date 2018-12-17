package com.jpp.moviespreview.screens.main.movies.paging

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePageInteractor
import com.jpp.moviespreview.screens.main.movies.UiMovieSection
import javax.inject.Inject


/**
 * Factory class to create the DataSource that will provide the pages to show in the movies section
 * of the application.
 * An instance of this class is injected into the ViewModels to hook the callbacks to the UI.
 */
class MoviesPagingDataSourceFactory @Inject constructor(private val moviePageInteractor: GetMoviePageInteractor) : DataSource.Factory<Int, Movie>() {


    private lateinit var dataSource: MoviesPagingDataSource
    lateinit var dataSourceLiveData: LiveData<MoviesDataSourceState>
    var section: UiMovieSection? = null
        set(value) {
            value?.let {
                dataSource = MoviesPagingDataSource(moviePageInteractor, movieSectionMapper.invoke(it))
                dataSourceLiveData = dataSource.getViewState()
            }
        }


    override fun create(): DataSource<Int, Movie> {
        if (::dataSource.isInitialized) {
            return dataSource
        }
        throw IllegalStateException("You need to provide a section to initialize the data source")
    }


    private val movieSectionMapper: (UiMovieSection) -> MovieSection = {
        when (it) {
            UiMovieSection.Playing -> MovieSection.Playing
            UiMovieSection.Popular -> MovieSection.Popular
            UiMovieSection.TopRated -> MovieSection.TopRated
            UiMovieSection.Upcoming -> MovieSection.Upcoming
        }
    }
}