package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.AccessTokenRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetAccessTokenUseCaseTest {

    @MockK
    private lateinit var accessTokenRepository: AccessTokenRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: GetAccessTokenUseCase

    @BeforeEach
    fun setUp() {
        subject = GetAccessTokenUseCase(accessTokenRepository, connectivityRepository)
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute()

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { accessTokenRepository.getAccessToken() } returns null

        val actual = subject.execute()

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should retrieve access token successfully`() = runBlocking {
        val expected: AccessToken = mockk()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { accessTokenRepository.getAccessToken() } returns expected

        val actual = subject.execute()

        assertTrue(actual is Try.Success)
        assertEquals(expected, actual.getOrNull())
    }
}
