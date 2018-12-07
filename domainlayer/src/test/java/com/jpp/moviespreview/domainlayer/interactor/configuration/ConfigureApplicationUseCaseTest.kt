package com.jpp.moviespreview.domainlayer.interactor.configuration

import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.repository.ConfigurationRepository
import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.interactor.ConfigureApplicationResult
import com.jpp.moviespreview.domainlayer.interactor.ConfigureApplicationUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class ConfigureApplicationUseCaseTest {

    companion object {

        @JvmStatic
        fun executeParameters() = listOf(
                Arguments.of(ConfigureApplicationResult.Success, mockk<AppConfiguration>(), true),
                Arguments.of(ConfigureApplicationResult.ErrorNoConnectivity, null, false),
                Arguments.of(ConfigureApplicationResult.ErrorUnknown, null, true)
        )
    }

    @MockK
    private lateinit var configRepository: ConfigurationRepository
    @MockK
    private lateinit var connectivityVerifier: ConnectivityVerifier

    private lateinit var subject: ConfigureApplicationUseCase


    @BeforeEach
    fun setUp() {
        subject = ConfigureApplicationUseCaseImpl(configRepository, connectivityVerifier)
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun execute(expected: ConfigureApplicationResult, appConfiguration: AppConfiguration?, connected: Boolean) {
        every { configRepository.getConfiguration() } returns appConfiguration
        every { connectivityVerifier.isConnectedToNetwork() } returns connected

        val actual = subject.invoke()

        assertEquals(expected, actual)
    }
}