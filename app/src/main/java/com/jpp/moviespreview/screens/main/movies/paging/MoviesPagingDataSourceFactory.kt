package com.jpp.moviespreview.screens.main.movies.paging

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePageInteractor
import java.util.concurrent.Executors
import javax.inject.Inject


/**
 * Factory class to create the DataSource that will provide the pages to show in the movies section
 * of the application.
 * An instance of this class is injected into the ViewModels to hook the callbacks to the UI.
 */
class MoviesPagingDataSourceFactory @Inject constructor(private val moviePageInteractor: GetMoviePageInteractor) : DataSource.Factory<Int, Movie>() {


    private lateinit var dataSource: MoviesPagingDataSource
    lateinit var dataSourceLiveData: LiveData<MoviesDataSourceState>

    override fun create(): DataSource<Int, Movie> {
        if (::dataSource.isInitialized) {
            return dataSource
        }
        throw IllegalStateException("You need to provide a section to initialize the data source")
    }


    fun <T> getMovieList(movieSection: MovieSection, mapper: (Movie) -> T): LiveData<PagedList<T>> {

        if (::dataSource.isInitialized) {
            dataSource.invalidate()
        }

        dataSource = MoviesPagingDataSource(moviePageInteractor, movieSection)
        dataSourceLiveData = dataSource.getViewState()

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(1)
                .build()

        return LivePagedListBuilder(map { mapper.invoke(it) }, config)
                .setFetchExecutor(Executors.newFixedThreadPool(5))
                .build()
    }
}