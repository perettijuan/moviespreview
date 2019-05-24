package com.jpp.mpdomain.usecase.session

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpdomain.usecase.session.GetAuthenticationDataUseCase.AuthenticationDataResult.*
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

//TODO JPP delete me?
@ExtendWith(MockKExtension::class)
class GetAuthenticationDataUseCaseTest {

    @RelaxedMockK
    private lateinit var sessionRepository: SessionRepository

    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository

    lateinit var subject: GetAuthenticationDataUseCase

    @BeforeEach
    fun setUp() {
        subject = GetAuthenticationDataUseCase.Impl(sessionRepository, connectivityRepository)
    }

//    @Test
//    fun `Should check connectivity before fetching AT and return ErrorNoConnectivity`() {
//        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected
//
//        subject.getAuthenticationData().let { result ->
//            verify(exactly = 0) { sessionRepository.getAccessToken() }
//            assertEquals(ErrorNoConnectivity, result)
//        }
//    }
//
//    @Test
//    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
//        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
//        every { sessionRepository.getAccessToken() } returns null
//
//        subject.getAuthenticationData().let { result ->
//            verify(exactly = 1) { sessionRepository.getAccessToken() }
//            assertEquals(ErrorUnknown, result)
//        }
//    }
//
//    @Test
//    fun `Should return Success when connected to network and an can get AT`() {
//        val aToken = AccessToken(
//                success = true,
//                expires_at = "",
//                request_token = "requestToken"
//        )
//        val authUrl = "anAuthUrl"
//        val authRedirect = "aRedirectUrl"
//        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
//        every { sessionRepository.getAccessToken() } returns aToken
//        every { sessionRepository.getAuthenticationUrl(any()) } returns authUrl
//        every { sessionRepository.getAuthenticationRedirection() } returns authRedirect
//
//        subject.getAuthenticationData().let { result ->
//            verify(exactly = 1) { sessionRepository.getAccessToken() }
//            verify(exactly = 1) { sessionRepository.getAuthenticationUrl(aToken) }
//            assertTrue(result is Success)
//            assertEquals((result as Success).authenticationURL, authUrl)
//            assertEquals((result).redirectionUrl, authRedirect)
//        }
//    }
//
//    @Test
//    fun `Should return ErrorUnknown when connected to network and an can get AT with error`() {
//        val aToken = mockk<AccessToken>()
//        every { aToken.success } returns false
//        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
//        every { sessionRepository.getAccessToken() } returns aToken
//
//        subject.getAuthenticationData().let { result ->
//            verify(exactly = 1) { sessionRepository.getAccessToken() }
//            assertEquals(ErrorUnknown, result)
//        }
//    }
}