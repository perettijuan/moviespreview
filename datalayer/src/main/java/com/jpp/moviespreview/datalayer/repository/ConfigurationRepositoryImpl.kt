package com.jpp.moviespreview.datalayer.repository

import com.jpp.moviespreview.datalayer.AppConfiguration

class ConfigurationRepositoryImpl(private val serverRepository: ConfigurationRepository) : ConfigurationRepository {
    override fun getConfiguration(): AppConfiguration? {
        return serverRepository.getConfiguration()
    }
}