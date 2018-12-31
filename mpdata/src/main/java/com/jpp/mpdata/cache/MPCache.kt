package com.jpp.mpdata.cache

import android.content.Context
import android.content.SharedPreferences
import com.jpp.moviespreview.common.extensions.and
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.MovieDAO
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdata.cache.timestamps.MPTimestamps
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MPCache @Inject constructor(private val context: Context,
                                  private val timestamps: MPTimestamps,
                                  private val roomDatabase: MPRoomDataBase,
                                  private val adapter: RoomModelAdapter)
    : MoviesDb {


    override fun getMoviePageForSection(page: Int, section: MovieSection): MoviePage? {
        return if (isCurrentMovieTypeStored(section) && timestamps.isMoviePageUpToDate()) {
            withMovieDao {
                getMoviePage(page)
                        ?.and { getMoviesFromPage(it.page) }
                        ?.let { pair ->
                            transformWithAdapter { pair.second?.let { movies -> adaptDBMoviePageToDataMoviePage(pair.first, movies) } }
                        }
            }
        } else {
            null
        }
    }

    override fun saveMoviePageForSection(moviePage: MoviePage, section: MovieSection) {
        withMovieDao {
            deleteAllMovies()
            deleteAllPages()
            insertMoviePage(transformWithAdapter { adaptDataMoviePageToDBMoviePage(moviePage) })
            insertMovies(moviePage.results.map { movie -> transformWithAdapter { adaptDataMovieToDBMovie(movie, moviePage.page) } })
        }.run {
            updateCurrentMovieTypeStored(section)
        }
    }


    /**
     * The DB only stores one movie type at the time. This means that this class ensures
     * that the DataBase will have stored only one type of movie page at the time.
     */
    private fun isCurrentMovieTypeStored(movieType: MovieSection): Boolean =
        withSharedPreferences {
            getString(MOVIE_TYPE_STORED_KEY, null)?.equals(movieType) ?: false
        }

    private fun updateCurrentMovieTypeStored(movieType: MovieSection) {
        withSharedPreferencesEditor {
            putString(MOVIE_TYPE_STORED_KEY, movieType.toString())
            apply()
        }
    }

    private fun <T> transformWithAdapter(action: RoomModelAdapter.() -> T): T = with(adapter) { action.invoke(this) }

    private fun <T> withMovieDao(action: MovieDAO.() -> T): T = with(roomDatabase.moviesDao()) { action.invoke(this) }

    private fun <T> withSharedPreferences(action: SharedPreferences.() -> T): T = with(getSharedPreferences()) { action.invoke(this) }

    private fun <T> withSharedPreferencesEditor(action: SharedPreferences.Editor.() -> T): T = with(getSharedPreferences().edit()) { action.invoke(this) }

    private fun getSharedPreferences(): SharedPreferences = context.getSharedPreferences("mp_database", Context.MODE_PRIVATE)

    companion object {
        private const val MOVIE_TYPE_STORED_KEY = "MPDatabase:MovieType"
    }
}