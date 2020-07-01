package com.jpp.mpdata.cache

import android.util.SparseArray
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomDomainAdapter
import com.jpp.mpdata.datasources.moviepage.MoviesDb
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection

/**
 * [MoviesDb] implementation with a cache mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class MoviesCache(
    roomDatabase: MPRoomDataBase,
    private val toDomain: RoomDomainAdapter,
    private val toRoom: DomainRoomAdapter,
    private val timestampHelper: CacheTimestampHelper
) : MoviesDb {

    private val moviesDao = roomDatabase.moviesDao()
    private val favoriteMovies = SparseArray<MoviePage>()
    private val ratedMovies = SparseArray<MoviePage>()
    private val watchlist = SparseArray<MoviePage>()

    override fun getMoviePageForSection(page: Int, section: MovieSection): MoviePage? {
        val dbMoviePage =
            moviesDao.getMoviePage(page, section.name, timestampHelper.now()) ?: return null
        val dbMoviesInPage = moviesDao.getMoviesFromPage(dbMoviePage.id) ?: return null
        return toDomain.moviePage(dbMoviePage, dbMoviesInPage)
    }

    override fun flushAllPagesInSection(section: MovieSection) {
        moviesDao.deleteAllPagesInSection(section.name)
    }

    override fun saveMoviePageForSection(moviePage: MoviePage, section: MovieSection) {
        val dbMoviePage = toRoom.moviePage(
            dataMoviePage = moviePage,
            sectionName = section.name,
            dueDate = timestampHelper.moviePagesRefreshTimestamp()
        )
        val pageId = moviesDao.insertMoviePage(dbMoviePage)
        val dbMovies = moviePage.results.map { domainMovie ->
            toRoom.movie(domainMovie, pageId)
        }
        moviesDao.insertMovies(dbMovies)
    }

    override fun getFavoriteMovies(page: Int): MoviePage? = favoriteMovies[page]

    override fun saveFavoriteMoviesPage(page: Int, moviePage: MoviePage) {
        favoriteMovies.put(page, moviePage)
    }

    override fun getRatedMovies(page: Int): MoviePage? = ratedMovies[page]

    override fun saveRatedMoviesPage(page: Int, moviePage: MoviePage) {
        ratedMovies.put(page, moviePage)
    }

    override fun getWatchlistMoviePage(page: Int): MoviePage? = watchlist[page]

    override fun saveWatchlistMoviePage(page: Int, moviePage: MoviePage) {
        watchlist.put(page, moviePage)
    }

    override fun flushFavoriteMoviePages() {
        favoriteMovies.clear()
    }

    override fun flushRatedMoviePages() {
        ratedMovies.clear()
    }

    override fun flushWatchlistMoviePages() {
        watchlist.clear()
    }

    /**
     * @return a Long that represents the expiration date of the movies page data stored in the
     * device.
     */
    private fun CacheTimestampHelper.moviePagesRefreshTimestamp(): Long =
        now() + moviePagesRefreshTime()
}
