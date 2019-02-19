package com.jpp.mpdata.repository.configuration

import com.jpp.mpdomain.AppConfiguration

//TODO JPP -> this should live in the data module
interface ConfigurationApi {
    fun getAppConfiguration(): AppConfiguration?
}