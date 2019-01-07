package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.MovieDAO
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.movies.MoviesDb


class MoviesCache(private val roomDatabase: MPRoomDataBase,
                  private val adapter: RoomModelAdapter,
                  private val timestampHelper: CacheTimestampHelper) : MoviesDb {


    override fun getMoviePageForSection(page: Int, section: MovieSection): MoviePage? {
        return withMovieDao {
            getMoviePage(page, section.name, now())?.let { dbMoviePage ->
                getMoviesFromPage(dbMoviePage.page)?.let { movieList ->
                    transformWithAdapter { adaptDBMoviePageToDataMoviePage(dbMoviePage, movieList) }
                }
            }
        }
    }

    override fun saveMoviePageForSection(moviePage: MoviePage, section: MovieSection) {
        withMovieDao {
            insertMoviePage(transformWithAdapter {
                adaptDataMoviePageToDBMoviePage(moviePage, section.name, moviePagesRefreshTime())
            })
        }.let {
            moviePage.results.map { movie -> transformWithAdapter { adaptDataMovieToDBMovie(movie, it) } }
        }.also {
            withMovieDao { insertMovies(it) }
        }
    }


    private fun <T> transformWithAdapter(action: RoomModelAdapter.() -> T): T = with(adapter) { action.invoke(this) }

    private fun <T> withMovieDao(action: MovieDAO.() -> T): T = with(roomDatabase.moviesDao()) { action.invoke(this) }

    private fun now() = timestampHelper.now()

    private fun moviePagesRefreshTime() = with(timestampHelper) { now() + moviePagesRefreshTime() }
}