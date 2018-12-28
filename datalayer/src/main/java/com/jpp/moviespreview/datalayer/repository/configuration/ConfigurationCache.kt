package com.jpp.moviespreview.datalayer.repository.configuration

import com.jpp.moviespreview.domainlayer.AppConfiguration

interface ConfigurationCache {
    fun getConfiguration(): AppConfiguration?
    fun updateAppConfiguration(appConfiguration: AppConfiguration)
}