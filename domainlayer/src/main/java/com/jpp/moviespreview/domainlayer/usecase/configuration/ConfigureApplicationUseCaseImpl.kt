package com.jpp.moviespreview.domainlayer.usecase.configuration

import com.jpp.moviespreview.datalayer.repository.ConfigurationRepository
import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.usecase.ConfigureApplicationResult
import com.jpp.moviespreview.domainlayer.usecase.ConfigureApplicationUseCase
import com.jpp.moviespreview.domainlayer.usecase.EmptyParam

class ConfigureApplicationUseCaseImpl(private val configRepository: ConfigurationRepository,
                                      private val connectivityVerifier: ConnectivityVerifier) : ConfigureApplicationUseCase {

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