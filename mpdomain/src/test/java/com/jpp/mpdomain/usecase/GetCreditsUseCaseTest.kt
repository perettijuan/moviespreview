package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.CreditsRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetCreditsUseCaseTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    @MockK
    private lateinit var creditsRepository: CreditsRepository

    private lateinit var subject: GetCreditsUseCase

    @BeforeEach
    fun setUp() {
        subject = GetCreditsUseCase(
            connectivityRepository,
            creditsRepository
        )
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { creditsRepository.getCreditsForMovie(any()) } returns null

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should retrieve movie credits`() {
        val expected = Credits(
            id = 1.0,
            cast = listOf(),
            crew = listOf()
        )

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { creditsRepository.getCreditsForMovie(1.0) } returns expected

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Success)
        assertEquals(expected, actual.getOrNull())
    }

}