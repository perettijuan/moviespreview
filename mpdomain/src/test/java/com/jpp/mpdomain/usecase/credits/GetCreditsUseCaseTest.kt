package com.jpp.mpdomain.usecase.credits

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.CreditsRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetCreditsUseCaseTest {

    @RelaxedMockK
    private lateinit var credRepository: CreditsRepository
    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: GetCreditsUseCase

    @BeforeEach
    fun setUp() {
        subject = GetCreditsUseCase.Impl(credRepository, connectivityRepository)
    }

    @Test
    fun `Should check connectivity before fetching credits and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.getCreditsForMovie(1.toDouble()).let { result ->
            verify(exactly = 0) { credRepository.getCreditsForMovie(any()) }
            assertEquals(GetCreditsResult.ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { credRepository.getCreditsForMovie(any()) } returns null

        subject.getCreditsForMovie(1.toDouble()).let { result ->
            verify(exactly = 1) { credRepository.getCreditsForMovie(any()) }
            assertEquals(GetCreditsResult.ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected to network and an can get credits`() {
        val person = mockk<Credits>()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { credRepository.getCreditsForMovie(any()) } returns person

        subject.getCreditsForMovie(1.toDouble()).let { result ->
            verify(exactly = 1) { credRepository.getCreditsForMovie(any()) }
            assertTrue(result is GetCreditsResult.Success)
            assertEquals((result as GetCreditsResult.Success).credits, person)
        }
    }

}