package com.jpp.moviespreview.domainlayer.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.jpp.moviespreview.domainlayer.*
import com.jpp.moviespreview.domainlayer.ds.movie.MoviesDataSourceState


/**
 * Repository definition to retrieve the [ImagesConfiguration] from
 * the data module.
 */
interface ConfigurationRepository {

    sealed class ConfigurationRepositoryOutput {
        object Error : ConfigurationRepositoryOutput()
        data class Success(val config: AppConfiguration) : ConfigurationRepositoryOutput()
    }


    fun getConfiguration(): ConfigurationRepositoryOutput
}

/**
 * Repository definition to handle all the data storage related to [Movie]s.
 */
interface MoviesRepository {

    sealed class MoviesRepositoryOutput {
        object Error : MoviesRepositoryOutput()
        data class MoviePageRetrieved(val page: MoviePage) : MoviesRepositoryOutput()
        data class MovieDetailsRetrieved(val detail: MovieDetail) : MoviesRepositoryOutput()
    }

    fun getNowPlayingMoviePage(page: Int): MoviesRepositoryOutput
    fun getPopularMoviePage(page: Int): MoviesRepositoryOutput
    fun getTopRatedMoviePage(page: Int): MoviesRepositoryOutput
    fun getUpcomingMoviePage(page: Int): MoviesRepositoryOutput
    fun getMovieDetail(movieId: Double): MoviesRepositoryOutput
    fun updateNowPlayingMoviePage(moviePage: MoviePage)
    fun updatePopularMoviePage(moviePage: MoviePage)
    fun updateTopRatedMoviePage(moviePage: MoviePage)
    fun updateUpcomingMoviePage(moviePage: MoviePage)
    fun updateMovieDetail(movieDetail: MovieDetail)
}


/***********************/


interface MovieListRepository {
    /**
     * Retrieves a [MovieListing] that can be use to show a list of movies retrieved from the server.
     * [section] indicates the section of interest for the request.
     * [targetBackdropSize] indicates the size target of the backdrop images of the Movies. Needed to configure the
     * backdrop URL path.
     * [targetPosterSize] indicates the size target of the backdrop images of the Movies. Needed to configure the
     * poster URL path.
     */
    fun <T> moviePageOfSection(section: MovieSection, targetBackdropSize: Int, targetPosterSize: Int, mapper: (Movie) -> T): MovieListing<T>
}

/**
 * Data class that models the response of the repository layer when the feature is requesting data as
 * a list backed by the paging library.
 */
data class MovieListing<T>(
        // the LiveData of paged lists for the UI to observe
        val pagedList: LiveData<PagedList<T>>,
        val dataSourceLiveData: LiveData<MoviesDataSourceState>
)
