package com.jpp.mpaccount.login

import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.TestAccountCoroutineDispatchers
import com.jpp.mpdomain.AccessToken
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
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

    @RelaxedMockK
    private lateinit var loginInteractor: LoginInteractor

    private val lvLoginEvent = MutableLiveData<LoginInteractor.LoginEvent>()
    private val lvOauthEvent = MutableLiveData<LoginInteractor.OauthEvent>()

    private lateinit var subject: LoginViewModel


    @BeforeEach
    fun setUp() {
        every { loginInteractor.loginEvents } returns lvLoginEvent
        every { loginInteractor.oauthEvents } returns lvOauthEvent

        subject = LoginViewModel(
                TestAccountCoroutineDispatchers(),
                loginInteractor
        )

        /*
         * Since the ViewModel uses a MediatorLiveData, we need to have
         * an observer on the view states attached all the time in order
         * to get notifications.
         */
        subject.viewStates.observeForever { }
    }

    @Test
    fun `Should show not connected when disconnection detected during login`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvLoginEvent.postValue(LoginInteractor.LoginEvent.NotConnectedToNetwork)

        assertEquals(LoginViewState.ShowNotConnected, viewStatePosted)
    }

    @Test
    fun `Should navigate to user account screen when login is successful`() {
        var eventPosted: LoginNavigationEvent? = null

        subject.navEvents.observeWith { eventPosted = it }

        lvLoginEvent.postValue(LoginInteractor.LoginEvent.LoginSuccessful)

        assertEquals(LoginNavigationEvent.ContinueToUserAccount, eventPosted)
    }

    @Test
    fun `Should show login error when error detected during login`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvLoginEvent.postValue(LoginInteractor.LoginEvent.LoginError)

        assertEquals(LoginViewState.ShowLoginError, viewStatePosted)
    }


    @Test
    fun `Should show not connected when disconnection detected during oauth`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvOauthEvent.postValue(LoginInteractor.OauthEvent.NotConnectedToNetwork)

        assertEquals(LoginViewState.ShowNotConnected, viewStatePosted)
    }

    @Test
    fun `Should show oauth state when oauth data becomes available`() {
        var viewStatePosted: LoginViewState? = null

        val oauthEvent = LoginInteractor.OauthEvent.OauthSuccessful(
                url = "aUrl",
                interceptUrl = "anInterceptUrl",
                accessToken = mockk()
        )

        val expected = LoginViewState.ShowOauth(
                url = "aUrl",
                interceptUrl = "anInterceptUrl",
                reminder = false
        )

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvOauthEvent.postValue(oauthEvent)

        assertEquals(expected, viewStatePosted)
    }

    @Test
    fun `Should show oauth state as reminder when new oauth data becomes available`() {
        var viewStatePosted = mutableListOf<LoginViewState>()

        val firstOauthEvent = LoginInteractor.OauthEvent.OauthSuccessful(
                url = "aFirstUrl",
                interceptUrl = "aFirstInterceptUrl",
                accessToken = mockk()
        )

        val secondOauthEvent = LoginInteractor.OauthEvent.OauthSuccessful(
                url = "aSecondUrl",
                interceptUrl = "aSecondInterceptUrl",
                accessToken = mockk()
        )

        val expectedFirst = LoginViewState.ShowOauth(
                url = "aFirstUrl",
                interceptUrl = "aFirstInterceptUrl",
                reminder = false
        )

        val expectedSecond = LoginViewState.ShowOauth(
                url = "aSecondUrl",
                interceptUrl = "aSecondInterceptUrl",
                reminder = true
        )


        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted.add(viewState) } }

        lvOauthEvent.postValue(firstOauthEvent)
        lvOauthEvent.postValue(secondOauthEvent)

        assertEquals(2, viewStatePosted.size)
        assertEquals(viewStatePosted[0], expectedFirst)
        assertEquals(viewStatePosted[1], expectedSecond)
    }

    @Test
    fun `Should show login error when error detected during oauth`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvOauthEvent.postValue(LoginInteractor.OauthEvent.OauthError)

        assertEquals(LoginViewState.ShowLoginError, viewStatePosted)
    }

    @Test
    fun `Should verify user logged in and push loading state in onInit`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onInit()

        assertEquals(LoginViewState.ShowLoading, viewStatePosted)
        verify { loginInteractor.verifyUserLogged() }
    }

    @Test
    fun `Should fetch oauth data when user is ready to login`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvLoginEvent.postValue(LoginInteractor.LoginEvent.ReadyToLogin)

        assertEquals(LoginViewState.ShowLoading, viewStatePosted)
    }

    @Test
    fun `Should ask interactor to perform login and push loading state when user approved redirected URL`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        // pre-condition: a oauth state needs to be pushed first
        val accessToken = mockk<AccessToken>()
        val oauthEvent = LoginInteractor.OauthEvent.OauthSuccessful(
                url = "aUrl",
                interceptUrl = "anInterceptUrl",
                accessToken = accessToken
        )

        lvOauthEvent.postValue(oauthEvent)

        subject.onUserRedirectedToUrl("http://someUrl?approved=true")

        assertEquals(LoginViewState.ShowLoading, viewStatePosted)
        verify { loginInteractor.loginUser(accessToken) }
    }

    @Test
    fun `Should push error state when user approved redirected URL without access token`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onUserRedirectedToUrl("http://someUrl?approved=true")

        assertEquals(LoginViewState.ShowLoginError, viewStatePosted)
    }

    @Test
    fun `Should fetch new Oauth data when user rejected access token `() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onUserRedirectedToUrl("http://someUrl?denied=true")

        assertEquals(LoginViewState.ShowLoading, viewStatePosted)
        verify { loginInteractor.fetchOauthData() }
    }

    @Test
    fun `Should push error state when user redirected to invalid URL`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onUserRedirectedToUrl("http://someUrlInvalid")

        assertEquals(LoginViewState.ShowLoginError, viewStatePosted)
    }
}