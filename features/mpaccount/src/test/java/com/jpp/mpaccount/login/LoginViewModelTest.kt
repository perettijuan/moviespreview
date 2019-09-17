package com.jpp.mpaccount.login

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.R
import com.jpp.mpaccount.TestAccountCoroutineDispatchers
import com.jpp.mptestutils.CoroutineTestExtention
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

@ExtendWith(
        MockKExtension::class,
        InstantTaskExecutorExtension::class,
        CoroutineTestExtention::class
)
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
        subject.viewState.observeForever { }
    }

    @Test
    fun `Should show not connected when disconnection detected during login`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvLoginEvent.postValue(LoginInteractor.LoginEvent.NotConnectedToNetwork)

        assertNotNull(viewStatePosted)
        assertEquals(R.string.login_generic, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.oauthViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should retry to fetch data when not connected and retry is executed`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvLoginEvent.postValue(LoginInteractor.LoginEvent.NotConnectedToNetwork)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify { loginInteractor.verifyUserLogged() }
        } ?: fail()
    }

    @Test
    fun `Should navigate to user account screen when login is successful`() {
        var eventPosted: ContinueToUserAccount? = null

        subject.navEvents.observeWith { eventPosted = it }

        lvLoginEvent.postValue(LoginInteractor.LoginEvent.LoginSuccessful)

        assertNotNull(eventPosted)
    }

    @Test
    fun `Should show login error when error detected during login`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvLoginEvent.postValue(LoginInteractor.LoginEvent.LoginError)

        assertNotNull(viewStatePosted)
        assertEquals(R.string.login_generic, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.oauthViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should retry to fetch data when error unknown and retry is executed`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvLoginEvent.postValue(LoginInteractor.LoginEvent.LoginError)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify { loginInteractor.verifyUserLogged() }
        } ?: fail()
    }


    @Test
    fun `Should show not connected when disconnection detected during oauth`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvOauthEvent.postValue(LoginInteractor.OauthEvent.NotConnectedToNetwork)

        assertNotNull(viewStatePosted)
        assertEquals(R.string.login_generic, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.oauthViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should show oauth state when oauth data becomes available`() {
        var viewStatePosted: LoginViewState? = null

        val oauthEvent = LoginInteractor.OauthEvent.OauthSuccessful(
                url = "aUrl",
                interceptUrl = "anInterceptUrl",
                accessToken = mockk()
        )

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvOauthEvent.postValue(oauthEvent)

        assertNotNull(viewStatePosted)
        assertEquals(R.string.login_generic, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)

        assertEquals(View.VISIBLE, viewStatePosted?.oauthViewState?.visibility)
        assertEquals("aUrl", viewStatePosted?.oauthViewState?.url)
        assertEquals("anInterceptUrl", viewStatePosted?.oauthViewState?.interceptUrl)
        assertEquals(false, viewStatePosted?.oauthViewState?.reminder)

    }


    @Test
    fun `Should show login error when error detected during oauth`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvOauthEvent.postValue(LoginInteractor.OauthEvent.OauthError)

        assertNotNull(viewStatePosted)
        assertEquals(R.string.login_generic, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.oauthViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should verify user logged in and push loading state in onInit`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(R.string.login_generic, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.oauthViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.loadingVisibility)
        verify { loginInteractor.verifyUserLogged() }
    }

    @Test
    fun `Should fetch oauth data when user is ready to login`() {
        var viewStatePosted: LoginViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvLoginEvent.postValue(LoginInteractor.LoginEvent.ReadyToLogin)

        assertNotNull(viewStatePosted)
        assertEquals(R.string.login_generic, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.oauthViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.loadingVisibility)
    }
}