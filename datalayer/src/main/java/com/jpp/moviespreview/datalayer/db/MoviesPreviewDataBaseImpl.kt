package com.jpp.moviespreview.datalayer.db

import android.content.Context
import androidx.room.Room
import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.MoviePage
import com.jpp.moviespreview.datalayer.db.room.MPRoomDataBase
import com.jpp.moviespreview.datalayer.db.room.RoomModelAdapter

class MoviesPreviewDataBaseImpl(private val context: Context,
                                private val adapter: RoomModelAdapter) : MoviesPreviewDataBase {
    override fun isCurrentMovieTypeStored(movieType: MovieType): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateCurrentMovieTypeStored(movieType: MovieType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMoviePage(page: Int): MoviePage? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateMoviePage(page: MoviePage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearMoviePagesStored() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val roomDatabase by lazy {
        Room
                .databaseBuilder(context, MPRoomDataBase::class.java, "MPRoomDataBase")
                .build()
    }

    override fun getStoredAppConfiguration(): AppConfiguration? =
        roomDatabase
                .imageSizeDao()
                .getImageSizes()?.let {
                    adapter.adaptImageSizesToAppConfiguration(it)
                }

    override fun updateAppConfiguration(appConfiguration: AppConfiguration) {
        adapter
                .adaptAppConfigurationToImageSizes(appConfiguration)
                .let {
                    roomDatabase
                            .imageSizeDao()
                            .insertImageSizes(it)
                }
    }
}