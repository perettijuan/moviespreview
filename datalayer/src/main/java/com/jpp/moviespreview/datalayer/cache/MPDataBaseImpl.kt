package com.jpp.moviespreview.datalayer.cache

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.jpp.moviespreview.common.extensions.and
import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.MovieDetail
import com.jpp.moviespreview.datalayer.MoviePage
import com.jpp.moviespreview.datalayer.cache.room.*

class MPDataBaseImpl(private val context: Context,
                     private val adapter: RoomModelAdapter) : MPDataBase {


    private val roomDatabase by lazy {
        Room.databaseBuilder(context, MPRoomDataBase::class.java, "MPRoomDataBase")
                .build()
    }

    override fun getStoredAppConfiguration(): AppConfiguration? =
        withImageSizeDao {
            getImageSizes()?.let { imageSizeList ->
                transformWithAdapter { adaptImageSizesToAppConfiguration(imageSizeList) }
            }
        }

    override fun updateAppConfiguration(appConfiguration: AppConfiguration) {
        withImageSizeDao {
            insertImageSizes(transformWithAdapter { adaptAppConfigurationToImageSizes(appConfiguration) })
        }
    }

    override fun isCurrentMovieTypeStored(movieType: MovieType): Boolean =
        withSharedPreferences {
            getString(MOVIE_TYPE_STORED_KEY, null)?.equals(movieType) ?: false
        }

    override fun updateCurrentMovieTypeStored(movieType: MovieType) {
        withSharedPreferencesEditor {
            putString(MOVIE_TYPE_STORED_KEY, movieType.toString())
            apply()
        }
    }

    override fun getMoviePage(page: Int): MoviePage? =
        withMoviePageDao {
            getMoviePage(page)
        }?.and { moviePage ->
            withMovieDao { getMoviesFromPage(moviePage.page) }
        }?.let { pair ->
            transformWithAdapter { pair.second?.let { movies -> adaptDBMoviePageToDataMoviePage(pair.first, movies) } }
        }

    override fun updateMoviePage(page: MoviePage) {
        withMoviePageDao {
            insertMoviePage(transformWithAdapter { adaptDataMoviePageToDBMoviePage(page) })
        }.and {
            withMovieDao {
                insertMovies(page.results.map { movie -> transformWithAdapter { adaptDataMovieToDBMovie(movie, page.page) } })
            }
        }
    }

    override fun clearMoviePagesStored() {
        withMovieDao { deleteAllMovies() }
                .and { withMoviePageDao { deleteAllPages() } }
    }

    override fun getMovieDetail(movieDetailId: Double): MovieDetail? =
        withMovieDetailsDao {
            Pair(getMovieDetail(movieDetailId), getGenresForDetailId(movieDetailId))
        }.let { pair ->
            transformWithAdapter { pair.first?.let { movieDetail -> pair.second?.let { genres -> adaptDBMovieDetail(movieDetail, genres) } } }
        }


    override fun cleanMovieDetail(movieDetailId: Double) {
        withMovieDetailsDao {
            cleanMovieDetail(movieDetailId)
        }
    }

    override fun saveMovieDetail(movieDetail: MovieDetail) {
        withMovieDetailsDao {
            insertMovieDetail(transformWithAdapter { adaptDataMovieDetail(movieDetail) })
            insertMovieGenres(transformWithAdapter { movieDetail.genres.map { adaptDataGenre(it, movieDetail) } })
        }
    }


    private fun <T> withImageSizeDao(action: ImageSizeDAO.() -> T): T = with(roomDatabase.imageSizeDao()) { action.invoke(this) }

    private fun <T> withMoviePageDao(action: MoviePageDAO.() -> T): T = with(roomDatabase.moviePageDao()) { action.invoke(this) }

    private fun <T> withMovieDao(action: MovieDAO.() -> T): T = with(roomDatabase.moviesDao()) { action.invoke(this) }

    private fun <T> withMovieDetailsDao(action: MovieDetailDAO.() -> T): T = with(roomDatabase.movieDetailsDao()) { action.invoke(this) }

    private fun <T> transformWithAdapter(action: RoomModelAdapter.() -> T): T = with(adapter) { action.invoke(this) }

    private fun <T> withSharedPreferences(action: SharedPreferences.() -> T): T = with(getSharedPreferences()) { action.invoke(this) }

    private fun <T> withSharedPreferencesEditor(action: SharedPreferences.Editor.() -> T): T = with(getSharedPreferences().edit()) { action.invoke(this) }

    private fun getSharedPreferences(): SharedPreferences = context.getSharedPreferences("mp_database", Context.MODE_PRIVATE)

    companion object {
        private const val MOVIE_TYPE_STORED_KEY = "MPDatabase:MovieType"
    }
}