package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AppConfiguration

interface ConfigurationDb {
    fun getAppConfiguration(): AppConfiguration?
    fun saveAppConfiguration(appConfiguration: AppConfiguration)
}