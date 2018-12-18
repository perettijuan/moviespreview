package com.jpp.moviespreview.domainlayer.interactor.configuration

import com.jpp.moviespreview.datalayer.repository.ConfigurationRepository
import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.interactor.ConfigureApplicationResult
import com.jpp.moviespreview.domainlayer.interactor.ConfigureApplicationInteractor
import com.jpp.moviespreview.domainlayer.interactor.EmptyParam

class ConfigureApplicationInteractorImpl(private val configRepository: ConfigurationRepository,
                                         private val connectivityVerifier: ConnectivityVerifier) : ConfigureApplicationInteractor {

    override fun execute(parameter: EmptyParam?): ConfigureApplicationResult {
        return configRepository.getConfiguration()?.let {
            ConfigureApplicationResult.Success
        } ?: run {
            when (connectivityVerifier.isConnectedToNetwork()) {
                true -> ConfigureApplicationResult.ErrorUnknown
                else -> ConfigureApplicationResult.ErrorNoConnectivity
            }
        }
    }
}