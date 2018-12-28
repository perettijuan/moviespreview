package com.jpp.moviespreview.datalayer.repository

import com.jpp.moviespreview.domainlayer.AppConfiguration
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository

class ConfigurationRepositoryImpl(private val cacheRepository: ConfigurationRepository,
                                  private val serverRepository: ConfigurationRepository) : ConfigurationRepository {


    override fun getConfiguration(): ConfigurationRepository.ConfigurationRepositoryOutput {
        return cacheRepository.getConfiguration().let {
            when (it) {
                is ConfigurationRepository.ConfigurationRepositoryOutput.Success -> it
                else -> serverRepository.getConfiguration().apply {
                    if (this is ConfigurationRepository.ConfigurationRepositoryOutput.Success) {
                        cacheRepository.updateAppConfiguration(config)
                    }
                }
            }
        }
    }

    override fun updateAppConfiguration(appConfiguration: AppConfiguration) {
        throw UnsupportedOperationException("Updating AppConfiguration is not supported by the application")
    }
}