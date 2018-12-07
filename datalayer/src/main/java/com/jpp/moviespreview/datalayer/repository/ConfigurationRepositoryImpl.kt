package com.jpp.moviespreview.datalayer.repository

import com.jpp.moviespreview.datalayer.AppConfiguration

class ConfigurationRepositoryImpl(private val cacheRepository: ConfigurationRepository,
                                  private val serverRepository: ConfigurationRepository) : ConfigurationRepository {


    override fun getConfiguration(): AppConfiguration? {
        return cacheRepository.getConfiguration() ?: serverRepository.getConfiguration()?.apply {
            cacheRepository.updateAppConfiguration(this)
        }
    }

    override fun updateAppConfiguration(appConfiguration: AppConfiguration) {
        throw UnsupportedOperationException("Updating AppConfiguration is not supported by the application")
    }
}