package com.jpp.moviespreview.datalayer.repository

import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ConfigurationRepositoryTest {

    @RelaxedMockK
    private lateinit var dbRepository: ConfigurationRepository
    @MockK
    private lateinit var serverRepository: ConfigurationRepository

    private lateinit var subject: ConfigurationRepository


    @BeforeEach
    fun setUp() {
        subject = ConfigurationRepositoryImpl(dbRepository, serverRepository)
    }

    @Test
    fun `Should never use server data when data is stored locally`() {
        val expected = ConfigurationRepository.ConfigurationRepositoryOutput.Success(mockk())

        every { dbRepository.getConfiguration() } returns expected

        val actual = subject.getConfiguration()

        assertEquals(expected, actual)

        verify(exactly = 0) { serverRepository.getConfiguration() }
    }

    @Test
    fun `Should retrieve data from server and update the local DB when data is not stored`() {
        val expected = ConfigurationRepository.ConfigurationRepositoryOutput.Success(mockk())

        every { dbRepository.getConfiguration() } returns ConfigurationRepository.ConfigurationRepositoryOutput.Error
        every { serverRepository.getConfiguration() } returns expected

        val actual = subject.getConfiguration()

        assertEquals(expected, actual)
        assertNotNull(actual)

        verify { dbRepository.updateAppConfiguration(expected.config) }
    }

    @Test
    fun `Should return error when data is not stored and server fails`() {
        val expected = ConfigurationRepository.ConfigurationRepositoryOutput.Error

        every { dbRepository.getConfiguration() } returns ConfigurationRepository.ConfigurationRepositoryOutput.Error
        every { serverRepository.getConfiguration() } returns ConfigurationRepository.ConfigurationRepositoryOutput.Error

        val actual = subject.getConfiguration()

        assertEquals(expected, actual)
        assertNotNull(actual)

        verify(exactly = 0) { dbRepository.updateAppConfiguration(any()) }
    }
}