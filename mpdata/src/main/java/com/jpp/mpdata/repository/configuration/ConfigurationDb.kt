package com.jpp.mpdata.repository.configuration

import com.jpp.mpdomain.AppConfiguration

interface ConfigurationDb {
    fun getAppConfiguration(): AppConfiguration?
    fun saveAppConfiguration(appConfiguration: AppConfiguration)
}