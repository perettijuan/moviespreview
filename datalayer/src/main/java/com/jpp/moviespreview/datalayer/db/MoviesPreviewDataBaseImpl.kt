package com.jpp.moviespreview.datalayer.db

import android.content.Context
import androidx.room.Room
import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.db.room.MPRoomDataBase
import com.jpp.moviespreview.datalayer.db.room.RoomModelAdapter

class MoviesPreviewDataBaseImpl(private val context: Context,
                                private val adapter: RoomModelAdapter) : MoviesPreviewDataBase {

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