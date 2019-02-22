package com.jpp.mpdata.repository.configuration

import com.jpp.mpdomain.AppConfiguration

interface ConfigurationApi {
    fun getAppConfiguration(): AppConfiguration?
}