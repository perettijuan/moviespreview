package com.jpp.moviespreview.domainlayer

import com.jpp.moviespreview.datalayer.DataLogger

class DomainLogger {

    fun log(value: String) {
        DataLogger().log(value)
    }
}