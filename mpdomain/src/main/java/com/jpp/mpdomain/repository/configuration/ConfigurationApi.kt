package com.jpp.mpdomain.repository.configuration

import com.jpp.mpdomain.AppConfiguration

interface ConfigurationApi {
    fun getAppConfiguration(): AppConfiguration?
}