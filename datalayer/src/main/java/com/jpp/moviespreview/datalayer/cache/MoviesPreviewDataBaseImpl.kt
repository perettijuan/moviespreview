package com.jpp.moviespreview.datalayer.cache

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.MoviePage
import com.jpp.moviespreview.datalayer.cache.room.MPRoomDataBase
import com.jpp.moviespreview.datalayer.cache.room.RoomModelAdapter

class MoviesPreviewDataBaseImpl(private val context: Context,
                                private val adapter: RoomModelAdapter) : MoviesPreviewDataBase {


    private val roomDatabase by lazy {
        Room.databaseBuilder(context, MPRoomDataBase::class.java, "MPRoomDataBase")
                .build()
    }

    override fun getStoredAppConfiguration(): AppConfiguration? =
        roomDatabase
                .imageSizeDao()
                .getImageSizes()?.let {
                    adapter.adaptImageSizesToAppConfiguration(it)
                }

    override fun updateAppConfiguration(appConfiguration: AppConfiguration) {
        adapter.adaptAppConfigurationToImageSizes(appConfiguration)
                .let {
                    roomDatabase
                            .imageSizeDao()
                            .insertImageSizes(it)
                }
    }


    override fun isCurrentMovieTypeStored(movieType: MovieType): Boolean =
        with(getSharedPreferences()) {
            getString(MOVIE_TYPE_STORED_KEY, null)?.equals(movieType) ?: false
        }


    override fun updateCurrentMovieTypeStored(movieType: MovieType) {
        with(getSharedPreferences().edit()) {
            putString(MOVIE_TYPE_STORED_KEY, movieType.toString())
            apply()
        }
    }

    override fun getMoviePage(page: Int): MoviePage? =
        roomDatabase
                .moviePageDao()
                .getMoviePage(page)?.let { moviePage ->
                    roomDatabase
                            .moviesDao()
                            .getMoviesFromPage(moviePage.page)?.let {
                                adapter.adaptDBMoviePageToDataMoviePage(moviePage, it)
                            }
                }


    override fun updateMoviePage(page: MoviePage) {
        adapter.adaptDataMoviePageToDBMoviePage(page)
                .let {
                    roomDatabase
                            .moviePageDao()
                            .insertMoviePage(it)
                }
        page.results
                .map { movie -> adapter.adaptDataMovieToDBMovie(movie, page.page) }
                .forEach { dbMovie -> roomDatabase.moviesDao().insertMovie(dbMovie) }
    }

    override fun clearMoviePagesStored() {
        with(roomDatabase) {
            moviesDao().deleteAllMovies()
            moviePageDao().deleteAllPages()
        }
    }

    private fun getSharedPreferences(): SharedPreferences = context.getSharedPreferences("mp_database", Context.MODE_PRIVATE)

    companion object {
        private const val MOVIE_TYPE_STORED_KEY = "MPDatabase:MovieType"
    }
}