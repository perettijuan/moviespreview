package com.jpp.mpaccount.login

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.AccessTokenRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class LoginInteractorTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @MockK
    private lateinit var accessTokenRepository: AccessTokenRepository
    @MockK
    private lateinit var sessionRepository: SessionRepository


    private lateinit var subject: LoginInteractor

    @BeforeEach
    fun setUp() {
        subject = LoginInteractor(
                connectivityRepository,
                accessTokenRepository,
                sessionRepository)
    }

    @Test
    fun `Should post oauth no connectivity when trying to fetch oauth data disconnected`() {
        var eventPosted: LoginInteractor.OauthEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.oauthEvents.observeWith { eventPosted = it }

        subject.fetchOauthData()

        assertEquals(LoginInteractor.OauthEvent.NotConnectedToNetwork, eventPosted)
    }

    @Test
    fun `Should post oauth error when connected but unable to fetch access token`() {
        var eventPosted: LoginInteractor.OauthEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { accessTokenRepository.getAccessToken() } returns null

        subject.oauthEvents.observeWith { eventPosted = it }

        subject.fetchOauthData()

        assertEquals(LoginInteractor.OauthEvent.OauthError, eventPosted)
    }

    @Test
    fun `Should post oauth successful when connected and able to fetch access token`() {
        var eventPosted: LoginInteractor.OauthEvent? = null
        val accessToken = AccessToken(
                success = true,
                expires_at = "expiration",
                request_token = "aRequestToken"
        )

        val expected = LoginInteractor.OauthEvent.OauthSuccessful(
                url = "https://www.themoviedb.org/authenticate/aRequestToken?redirect_to=http://www.mp.com/approved",
                interceptUrl = "http://www.mp.com/approved",
                accessToken = accessToken
        )

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { accessTokenRepository.getAccessToken() } returns accessToken

        subject.oauthEvents.observeWith { eventPosted = it }

        subject.fetchOauthData()

        assertEquals(expected, eventPosted)
    }

    @Test
    fun `Should post login no connectivity when trying to login disconnected`() {
        var eventPosted: LoginInteractor.LoginEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.loginEvents.observeWith { eventPosted = it }

        subject.loginUser(mockk())

        assertEquals(LoginInteractor.LoginEvent.NotConnectedToNetwork, eventPosted)
    }

    @Test
    fun `Should post login error when trying to login connected but unable to create session`() {
        var eventPosted: LoginInteractor.LoginEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.createSession(any()) } returns null


        subject.loginEvents.observeWith { eventPosted = it }

        subject.loginUser(mockk())

        assertEquals(LoginInteractor.LoginEvent.LoginError, eventPosted)
    }

    @Test
    fun `Should post login successful when login connected and able to create session`() {
        var eventPosted: LoginInteractor.LoginEvent? = null
        val accessToken = mockk<AccessToken>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.createSession(any()) } returns mockk()


        subject.loginEvents.observeWith { eventPosted = it }

        subject.loginUser(accessToken)

        assertEquals(LoginInteractor.LoginEvent.LoginSuccessful, eventPosted)
        verify { sessionRepository.createSession(accessToken) }
    }
}