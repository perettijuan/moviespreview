package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.screens.main.movies.paging.MoviesPagingDataSourceFactory
import java.util.concurrent.Executors
import javax.inject.Inject

class MoviesFragmentViewModel @Inject constructor(private val pagingDataSourceFactory: MoviesPagingDataSourceFactory) : ViewModel() {

    private lateinit var viewState: LiveData<MoviesFragmentViewState>

    fun bindViewState(): LiveData<MoviesFragmentViewState> = viewState

    //TODO this should not be called this way, some mapping should happen
    fun getMovieList(movieSection: MovieSection): LiveData<PagedList<Movie>> {
        pagingDataSourceFactory.currentSection = movieSection

        viewState = Transformations.switchMap(pagingDataSourceFactory.dataSourceLiveData) {
            it.viewStateLiveData
        }

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2) // 2 pre-loads now
                .build()

        return LivePagedListBuilder(pagingDataSourceFactory, config)
                .setFetchExecutor(Executors.newFixedThreadPool(5))
                .build()
    }
}