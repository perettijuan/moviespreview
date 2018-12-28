package com.jpp.moviespreview.domainlayer.repository.movies

import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.repository.MovieListRepository
import com.jpp.moviespreview.domainlayer.repository.MovieListing
import java.util.concurrent.Executor

class MovieListRepositoryImpl(private val db: MoviesDb,
                              private val api: MoviesApi,
                              private val ioExecutor: Executor) : MovieListRepository {

    private fun insertResultInDb(movieSection: MovieSection, moviePage: MoviePage) {
        db.insertMoviePage(movieSection, moviePage)
    }

    override fun moviePageOfSection(section: MovieSection, targetBackdropSize: Int, targetPosterSize: Int): MovieListing {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = MoviesBoundaryCallback(
                section = section,
                handleResponse = this::insertResultInDb,
                ioExecutor = ioExecutor,
                moviesApi = api
        )


        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true) //This does actually nothing (and it sucks). -> placeholders can only be enabled if your DataSource provides total items count.
                .setPrefetchDistance(1)
                .build()

        val livePagedList = LivePagedListBuilder(db.moviesBySection(section), config)
                .setFetchExecutor(ioExecutor)
                .setBoundaryCallback(boundaryCallback)
                .build()

        //TODO JPP -> you have to map networkState and retryAllFailed
        return MovieListing(livePagedList)
    }
}