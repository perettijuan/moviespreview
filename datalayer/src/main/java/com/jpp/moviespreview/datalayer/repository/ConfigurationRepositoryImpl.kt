package com.jpp.moviespreview.datalayer.repository

import com.jpp.moviespreview.domainlayer.ImagesConfiguration
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository

class ConfigurationRepositoryImpl(private val cacheRepository: ConfigurationRepository,
                                  private val serverRepository: ConfigurationRepository) : ConfigurationRepository {


    override fun getConfiguration(): ImagesConfiguration? {
        return cacheRepository.getConfiguration() ?: serverRepository.getConfiguration()?.apply {
            cacheRepository.updateAppConfiguration(this)
        }
    }

    override fun updateAppConfiguration(imagesConfiguration: ImagesConfiguration) {
        throw UnsupportedOperationException("Updating AppConfiguration is not supported by the application")
    }
}