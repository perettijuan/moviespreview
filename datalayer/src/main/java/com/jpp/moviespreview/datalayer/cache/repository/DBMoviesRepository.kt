package com.jpp.moviespreview.datalayer.cache.repository

import com.jpp.moviespreview.datalayer.MoviePage
import com.jpp.moviespreview.datalayer.cache.MovieType
import com.jpp.moviespreview.datalayer.cache.MoviesPreviewDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.datalayer.repository.MoviesRepository

class DBMoviesRepository(private val mpCache: MPTimestamps,
                         private val mpDatabase: MoviesPreviewDataBase) : MoviesRepository {

    override fun getNowPlayingMoviePage(page: Int): MoviePage? = getMoviePageOrClearDataBaseIfNeeded(MovieType.NowPlaying, page)

    override fun getPopularMoviePage(page: Int): MoviePage? = getMoviePageOrClearDataBaseIfNeeded(MovieType.Popular, page)

    override fun getTopRatedMoviePage(page: Int): MoviePage? = getMoviePageOrClearDataBaseIfNeeded(MovieType.TopRated, page)

    override fun getUpcomingMoviePage(page: Int): MoviePage? = getMoviePageOrClearDataBaseIfNeeded(MovieType.Upcoming, page)

    override fun updateNowPlayingMoviePage(moviePage: MoviePage) = updateMoviePage(MovieType.NowPlaying, moviePage)

    override fun updatePopularMoviePage(moviePage: MoviePage) = updateMoviePage(MovieType.Popular, moviePage)

    override fun updateTopRatedMoviePage(moviePage: MoviePage) = updateMoviePage(MovieType.TopRated, moviePage)

    override fun updateUpcomingMoviePage(moviePage: MoviePage) = updateMoviePage(MovieType.Upcoming, moviePage)


    private fun updateMoviePage(movieType: MovieType, moviePage: MoviePage) {
        with(mpDatabase) {
            updateCurrentMovieTypeStored(movieType)
            updateMoviePage(moviePage)
        }.also {
            mpCache.updateMoviesInserted()
        }
    }

    private fun getMoviePageOrClearDataBaseIfNeeded(movieType: MovieType, page: Int): MoviePage? {
        return when (shouldRetrieveMoviePage(movieType)) {
            true -> mpDatabase.getMoviePage(page)
            else -> {
                mpDatabase.clearMoviePagesStored()
                null
            }
        }
    }

    private fun shouldRetrieveMoviePage(movieType: MovieType): Boolean =
        mpDatabase.isCurrentMovieTypeStored(movieType) && mpCache.areMoviesUpToDate()
}