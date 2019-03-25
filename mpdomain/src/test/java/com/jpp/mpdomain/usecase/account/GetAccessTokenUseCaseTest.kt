package com.jpp.mpdomain.usecase.account

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import com.jpp.mpdomain.usecase.account.GetAccessTokenUseCase.AccessTokenResult.*
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(MockKExtension::class)
class GetAccessTokenUseCaseTest {

    @RelaxedMockK
    private lateinit var sessionRepository: SessionRepository

    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository

    lateinit var subject: GetAccessTokenUseCase

    @BeforeEach
    fun setUp() {
        subject = GetAccessTokenUseCase.Impl(sessionRepository, connectivityRepository)
    }

    @Test
    fun `Should check connectivity before fetching AT and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.getAccessToken().let { result ->
            verify(exactly = 0) { sessionRepository.getAccessToken() }
            assertEquals(ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getAccessToken() } returns null

        subject.getAccessToken().let { result ->
            verify(exactly = 1) { sessionRepository.getAccessToken() }
            assertEquals(ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected to network and an can get AT`() {
        val aToken = mockk<AccessToken>()
        every { aToken.success } returns true
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getAccessToken() } returns aToken

        subject.getAccessToken().let { result ->
            verify(exactly = 1) { sessionRepository.getAccessToken() }
            assertTrue(result is Success)
            assertEquals((result as Success).accessToken, aToken)
        }
    }

    @Test
    fun `Should return Success when connected to network and an can get AT with error`() {
        val aToken = mockk<AccessToken>()
        every { aToken.success } returns false
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getAccessToken() } returns aToken

        subject.getAccessToken().let { result ->
            verify(exactly = 1) { sessionRepository.getAccessToken() }
            assertEquals(ErrorUnknown, result)
        }
    }
}