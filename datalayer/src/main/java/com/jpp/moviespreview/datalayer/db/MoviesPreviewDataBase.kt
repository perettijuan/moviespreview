package com.jpp.moviespreview.datalayer.db

import com.jpp.moviespreview.datalayer.AppConfiguration

interface MoviesPreviewDataBase {
    fun getStoredAppConfiguration() : AppConfiguration?
    fun updateAppConfiguration(appConfiguration: AppConfiguration)
}