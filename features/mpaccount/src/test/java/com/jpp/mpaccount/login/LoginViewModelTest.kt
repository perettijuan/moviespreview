package com.jpp.mpaccount.login

import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.TestAccountCoroutineDispatchers
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.AccessTokenRepository
import com.jpp.mpdomain.repository.MPConnectivityRepository
import com.jpp.mpdomain.repository.MPSessionRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
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

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class LoginViewModelTest {

    @MockK
    private lateinit var connectivityRepository: MPConnectivityRepository
    @RelaxedMockK
    private lateinit var sessionRepository: MPSessionRepository
    @RelaxedMockK
    private lateinit var accessTokenRepository: AccessTokenRepository


    private val lvSession = MutableLiveData<MPSessionRepository.SessionData>()
    private val lvAccessToken = MutableLiveData<AccessTokenRepository.AccessTokenData>()
    private val lvConnectivity = MutableLiveData<Connectivity>()

    private lateinit var subject: LoginViewModel


    @BeforeEach
    fun setUp() {
        every { connectivityRepository.data() } returns lvConnectivity
        every { sessionRepository.data() } returns lvSession
        every { accessTokenRepository.data() } returns lvAccessToken

        subject = LoginViewModel(
                TestAccountCoroutineDispatchers(),
                connectivityRepository,
                sessionRepository,
                accessTokenRepository
        )

        /*
         * Since the ViewModel uses a MediatorLiveData, we need to have
         * an observer on the view states attached all the time in order
         * to get notifications.
         */
        subject.viewStates.observeForever { }
    }

    @Test
    fun `Should navigate to previews when a session is available`() {
        var eventPosted: LoginNavigationEvent? = null

        subject.navEvents.observeWith { eventPosted = it }

        lvSession.postValue(MPSessionRepository.SessionData.CurrentSession(mockk()))

        assertEquals(LoginNavigationEvent.RemoveLogin, eventPosted)
    }

    @Test
    fun `Should navigate to previews when a session is created`() {
        var eventPosted: LoginNavigationEvent? = null

        subject.navEvents.observeWith { eventPosted = it }

        lvSession.postValue(MPSessionRepository.SessionData.SessionCreated(mockk()))

        assertEquals(LoginNavigationEvent.RemoveLogin, eventPosted)
    }

    @Test
    fun `Should push loading view state and retrieve access token when there's no session available`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvSession.postValue(MPSessionRepository.SessionData.NoCurrentSessionAvailable)

        assertEquals(LoginViewState.ShowLoading, viewStatePosted)
        verify { accessTokenRepository.getAccessToken() }
    }

    @Test
    fun `Should push unable to login when an error occurs while creating session`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvSession.postValue(MPSessionRepository.SessionData.UnableToCreateSession)

        assertEquals(LoginViewState.ShowLoginError, viewStatePosted)
    }

    @Test
    fun `Should push Oauth first step when an access token can be retrieved`() {
        val expectedUrl = "https://www.themoviedb.org/authenticate//aRequestToken?redirect_to=http://www.mp.com/approved"
        val expectedRedirectionUrl = "http://www.mp.com/approved"
        val accessTokenCreated = AccessToken(
                success = true,
                request_token = "aRequestToken",
                expires_at = "expirationDate"
        )
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvAccessToken.postValue(AccessTokenRepository.AccessTokenData.Success(accessTokenCreated))

        assertTrue(viewStatePosted is LoginViewState.ShowOauth)
        with (viewStatePosted as LoginViewState.ShowOauth) {
            assertEquals(expectedUrl, url)
            assertEquals(expectedRedirectionUrl, interceptUrl)
            assertEquals(accessTokenCreated, accessToken)
            assertFalse(reminder)
        }
    }

    @Test
    fun `Should push unable to login when an error occurs while fetching an access token`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvAccessToken.postValue(AccessTokenRepository.AccessTokenData.NoAccessTokenAvailable)

        assertEquals(LoginViewState.ShowLoginError, viewStatePosted)
    }

    @Test
    fun `Should push not connected when a disconnection event is detected`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvConnectivity.postValue(Connectivity.Disconnected)

        assertEquals(LoginViewState.ShowNotConnected, viewStatePosted)
    }

    @Test
    fun `Should verify if user logged in when re-connection event is detected`() {
        lvConnectivity.postValue(Connectivity.Connected)

        verify { sessionRepository.getCurrentSession() }
    }

    @Test
    fun `Should verify if user logged in on init`() {
        subject.onInit()

        verify { sessionRepository.getCurrentSession() }
    }

    @Test
    fun `Should post loading and create session when user redirected with approval`() {
        val redirectUrl = "https://somewrb.com?approved=true"
        val accessToken = AccessToken(
                success = true,
                request_token = "aRequestToken",
                expires_at = "expirationDate"
        )

        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onUserRedirectedToUrl(redirectUrl, accessToken)

        assertEquals(LoginViewState.ShowLoading, viewStatePosted)
        verify { sessionRepository.createAndStoreSession(accessToken) }
    }

    @Test
    fun `Should push Oauth first step with reminder when user has rejected access`() {
        val redirectUrl = "https://somewrb.com?denied=true"
        val expectedUrl = "https://www.themoviedb.org/authenticate//aRequestToken?redirect_to=http://www.mp.com/approved"
        val expectedRedirectionUrl = "http://www.mp.com/approved"
        val accessToken = AccessToken(
                success = true,
                request_token = "aRequestToken",
                expires_at = "expirationDate"
        )
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onUserRedirectedToUrl(redirectUrl, accessToken)

        assertTrue(viewStatePosted is LoginViewState.ShowOauth)
        with (viewStatePosted as LoginViewState.ShowOauth) {
            assertEquals(expectedUrl, url)
            assertEquals(expectedRedirectionUrl, interceptUrl)
            assertEquals(accessToken, accessToken)
            assertTrue(reminder)
        }
    }

    @Test
    fun `Should post error when an unknown redirection is detected`() {
        val redirectUrl = "https://somewrb.com?unknown"
        val accessToken = AccessToken(
                success = true,
                request_token = "aRequestToken",
                expires_at = "expirationDate"
        )

        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onUserRedirectedToUrl(redirectUrl, accessToken)

        assertEquals(LoginViewState.ShowLoginError, viewStatePosted)
    }

}