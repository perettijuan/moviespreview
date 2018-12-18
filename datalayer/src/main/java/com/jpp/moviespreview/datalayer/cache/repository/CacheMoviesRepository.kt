package com.jpp.moviespreview.datalayer.cache.repository


import com.jpp.moviespreview.datalayer.DataModelMapper
import com.jpp.moviespreview.datalayer.cache.MovieType
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository

class CacheMoviesRepository(private val mpCache: MPTimestamps,
                            private val mpDatabase: MPDataBase,
                            private val mapper: DataModelMapper) : MoviesRepository {

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
            updateMoviePage(mapper.mapDomainMoviePage(moviePage))
        }.also {
            mpCache.updateMoviesInserted()
        }
    }

    private fun getMoviePageOrClearDataBaseIfNeeded(movieType: MovieType, page: Int): MoviePage? {
        return when (shouldRetrieveMoviePage(movieType)) {
            true -> mpDatabase.getMoviePage(page)?.let { mapper.mapDataMoviePage(it) }
            else -> {
                mpDatabase.clearMoviePagesStored()
                null
            }
        }
    }

    private fun shouldRetrieveMoviePage(movieType: MovieType): Boolean =
        mpDatabase.isCurrentMovieTypeStored(movieType) && mpCache.areMoviesUpToDate()
}