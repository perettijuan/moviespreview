package com.jpp.moviespreview.datalayer.repository.configuration

import com.jpp.moviespreview.domainlayer.AppConfiguration
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository

class ConfigurationRepositoryImpl(private val configCache: ConfigurationCache,
                                  private val configServer: ConfigurationServer) : ConfigurationRepository {


    override fun getConfiguration(): ConfigurationRepository.ConfigurationRepositoryOutput {
        return configCache.getConfiguration()?.let {
            ConfigurationRepository.ConfigurationRepositoryOutput.Success(it)
        } ?: run {
            configServer.getAppConfiguration()?.let { appConfig ->
                configCache.updateAppConfiguration(appConfig)
                ConfigurationRepository.ConfigurationRepositoryOutput.Success(appConfig)
            } ?: run {
                ConfigurationRepository.ConfigurationRepositoryOutput.Error
            }
        }
    }
}