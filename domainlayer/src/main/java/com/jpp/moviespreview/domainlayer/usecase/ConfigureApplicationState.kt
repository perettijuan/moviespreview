package com.jpp.moviespreview.domainlayer.usecase

sealed class ConfigureApplicationState {
    object NoConnectivity : ConfigureApplicationState()
    object Unknown : ConfigureApplicationState()
    object Success : ConfigureApplicationState()
}