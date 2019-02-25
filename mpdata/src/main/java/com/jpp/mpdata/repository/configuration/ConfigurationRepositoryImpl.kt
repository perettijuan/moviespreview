package com.jpp.mpdata.repository.configuration

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.repository.ConfigurationRepository

class ConfigurationRepositoryImpl(private val configurationApi: ConfigurationApi,
                                  private val configurationDb: ConfigurationDb) : ConfigurationRepository {
    override fun getAppConfiguration(): AppConfiguration? {
        return configurationDb.getAppConfiguration() ?: run {
            configurationApi.getAppConfiguration()?.also { configurationDb.saveAppConfiguration(it) }
        }
    }
}