package com.jpp.moviespreview.datalayer.cache.repository


import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.domainlayer.MovieDetail
import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository

/**
 * [MoviesRepository] implementation with cache functionality.
 */
class CacheMoviesRepository(private val mpCache: MPTimestamps,
                            private val mpDatabase: MPDataBase) : MoviesRepository {


    override fun getNowPlayingMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput = getMoviePageOrClearDataBaseIfNeeded(MovieSection.Playing, page)

    override fun getPopularMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput = getMoviePageOrClearDataBaseIfNeeded(MovieSection.Popular, page)

    override fun getTopRatedMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput = getMoviePageOrClearDataBaseIfNeeded(MovieSection.TopRated, page)

    override fun getUpcomingMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput = getMoviePageOrClearDataBaseIfNeeded(MovieSection.Upcoming, page)

    override fun getMovieDetail(movieId: Double): MoviesRepository.MoviesRepositoryOutput {
        return when (mpCache.isMovieDetailUpToDate(movieId)) {
            true -> mpDatabase.getMovieDetail(movieId)
                    ?.let { MoviesRepository.MoviesRepositoryOutput.MovieDetailsRetrieved(it) }
                    ?: let { MoviesRepository.MoviesRepositoryOutput.Error }
            false -> {
                mpDatabase.cleanMovieDetail(movieId)
                MoviesRepository.MoviesRepositoryOutput.Error
            }
        }
    }

    override fun updateNowPlayingMoviePage(moviePage: MoviePage) = updateMoviePage(MovieSection.Playing, moviePage)

    override fun updatePopularMoviePage(moviePage: MoviePage) = updateMoviePage(MovieSection.Popular, moviePage)

    override fun updateTopRatedMoviePage(moviePage: MoviePage) = updateMoviePage(MovieSection.TopRated, moviePage)

    override fun updateUpcomingMoviePage(moviePage: MoviePage) = updateMoviePage(MovieSection.Upcoming, moviePage)

    override fun updateMovieDetail(movieDetail: MovieDetail) {
        with(mpDatabase) {
            saveMovieDetail(movieDetail)
        }
    }

    /**
     * Updates the [MoviePage] in the local storage and the timestamp for when the movie page
     * has been inserted.
     */
    private fun updateMoviePage(movieType: MovieSection, moviePage: MoviePage) {
        with(mpDatabase) {
            updateCurrentMovieTypeStored(movieType)
            updateMoviePage(moviePage)
        }.also {
            mpCache.updateMoviesInserted()
        }
    }

    /**
     * Verifies if the data stored in the database is valid (based on the timestamp) and retrieves it
     * if it is. If it is not valid, it clears the local storage in order to keep the database clean.
     */
    private fun getMoviePageOrClearDataBaseIfNeeded(movieType: MovieSection, page: Int): MoviesRepository.MoviesRepositoryOutput {
        return when (shouldRetrieveMoviePage(movieType)) {
            true -> mpDatabase.getMoviePage(page)
                    ?.let { MoviesRepository.MoviesRepositoryOutput.MoviePageRetrieved(it) }
                    ?: let { MoviesRepository.MoviesRepositoryOutput.Error }
            else -> {
                mpDatabase.clearMoviePagesStored()
                MoviesRepository.MoviesRepositoryOutput.Error
            }
        }
    }

    private fun shouldRetrieveMoviePage(movieType: MovieSection): Boolean =
        mpDatabase.isCurrentMovieTypeStored(movieType) && mpCache.areMoviesUpToDate()
}