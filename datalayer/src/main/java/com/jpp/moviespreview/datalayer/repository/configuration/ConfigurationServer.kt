package com.jpp.moviespreview.datalayer.repository.configuration

import com.jpp.moviespreview.domainlayer.AppConfiguration

interface ConfigurationServer {
    fun getAppConfiguration(): AppConfiguration?
}