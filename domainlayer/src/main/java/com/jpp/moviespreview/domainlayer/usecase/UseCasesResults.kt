package com.jpp.moviespreview.domainlayer.usecase

/***************************************************************************************************
 ************* Contains the definition of all the Results that Use Cases can return.  **************
 ***************************************************************************************************/

/**
 * Represents the results that can be returned by the [ConfigureApplicationUseCase]
 */
sealed class ConfigureApplicationResult {
    object ErrorNoConnectivity : ConfigureApplicationResult()
    object ErrorUnknown : ConfigureApplicationResult()
    object Success : ConfigureApplicationResult()
}