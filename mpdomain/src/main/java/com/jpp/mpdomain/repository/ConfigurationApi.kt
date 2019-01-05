package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AppConfiguration

interface ConfigurationApi {
    fun getAppConfiguration(): AppConfiguration?
}