package com.jpp.moviespreview.datalayer.repository

import com.jpp.moviespreview.domainlayer.ImagesConfiguration
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
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
        val expected = mockk<ImagesConfiguration>()

        every { dbRepository.getConfiguration() } returns expected

        val actual = subject.getConfiguration()

        assertEquals(expected, actual)

        verify(exactly = 0) { serverRepository.getConfiguration() }
    }

    @Test
    fun `Should retrieve data from server and update the local DB when data is not stored`() {
        val expected = mockk<ImagesConfiguration>()

        every { dbRepository.getConfiguration() } returns null
        every { serverRepository.getConfiguration() } returns expected

        val actual = subject.getConfiguration()

        assertEquals(expected, actual)
        assertNotNull(actual)

        verify { dbRepository.updateAppConfiguration(expected) }
    }

    @Test
    fun `Should retrieve null when data is not stored and server fails`() {
        every { dbRepository.getConfiguration() } returns null
        every { serverRepository.getConfiguration() } returns null

        val actual = subject.getConfiguration()

        assertNull(actual)

        verify(exactly = 0) { dbRepository.updateAppConfiguration(any()) }
    }
}