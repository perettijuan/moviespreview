package com.jpp.moviespreview.datalayer.repository

import com.jpp.moviespreview.datalayer.AppConfiguration

class ConfigurationRepositoryImpl(private val dbRepository: ConfigurationRepository,
                                  private val serverRepository: ConfigurationRepository) : ConfigurationRepository {


    override fun getConfiguration(): AppConfiguration? {
        return dbRepository.getConfiguration() ?: serverRepository.getConfiguration()?.apply {
            dbRepository.updateAppConfiguration(this)
        }
    }

    override fun updateAppConfiguration(appConfiguration: AppConfiguration) {
        throw UnsupportedOperationException("Updating AppConfiguration is not supported by the application")
    }
}