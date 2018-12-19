package com.jpp.moviespreview.domainlayer.interactor.configuration


import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.interactor.ConfigureApplication
import com.jpp.moviespreview.domainlayer.interactor.ConfigureApplicationResult
import com.jpp.moviespreview.domainlayer.interactor.EmptyParam
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository

class ConfigureApplicationImpl(private val configRepository: ConfigurationRepository,
                               private val connectivityVerifier: ConnectivityVerifier) : ConfigureApplication {

    override fun execute(parameter: EmptyParam): ConfigureApplicationResult {
        return configRepository.getConfiguration().let {
            when (it) {
                is ConfigurationRepository.ConfigurationRepositoryOutput.Success -> ConfigureApplicationResult.Success
                else -> {
                    when (connectivityVerifier.isConnectedToNetwork()) {
                        true -> ConfigureApplicationResult.ErrorUnknown
                        else -> ConfigureApplicationResult.ErrorNoConnectivity
                    }
                }
            }
        }
    }
}