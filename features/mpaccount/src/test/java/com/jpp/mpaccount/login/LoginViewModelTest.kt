package com.jpp.mpaccount.login

import android.view.View
import androidx.lifecycle.SavedStateHandle
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.usecase.GetAccessTokenUseCase
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
import com.jpp.mpdomain.usecase.LoginUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class,
    CoroutineTestExtension::class
)
class LoginViewModelTest {

    @RelaxedMockK
    private lateinit var getUserAccountUseCase: GetUserAccountUseCase

    @RelaxedMockK
    private lateinit var getAccessTokenUseCase: GetAccessTokenUseCase

    @RelaxedMockK
    private lateinit var loginUseCase: LoginUseCase

    @RelaxedMockK
    private lateinit var loginNavigator: LoginNavigator

    private lateinit var subject: LoginViewModel

    @BeforeEach
    fun setUp() {
        subject = LoginViewModel(
            getUserAccountUseCase,
            getAccessTokenUseCase,
            loginUseCase,
            loginNavigator,
            CoroutineTestExtension.testDispatcher,
            SavedStateHandle()
        )
    }

    @Test
    fun `Should show not connected when not connected while verifying user logged in`() {
        var viewStatePosted: LoginViewState? = null

        coEvery { getUserAccountUseCase.execute() } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.oauthViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should show unknown error when verifying user logged in`() {
        var viewStatePosted: LoginViewState? = null

        coEvery { getUserAccountUseCase.execute() } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.oauthViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should redirect to user account when user logged in`() {
        coEvery { getUserAccountUseCase.execute() } returns Try.Success(mockk())

        subject.onInit()

        loginNavigator.navigateToUserAccount()
    }

    @Test
    fun `Should retry to fetch data when not connected and retry is executed`() {
        var viewStatePosted: LoginViewState? = null

        coEvery { getUserAccountUseCase.execute() } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getUserAccountUseCase.execute() }
        } ?: fail()
    }

    @Test
    fun `Should show not connected when disconnection detected during oauth`() {
        var viewStatePosted: LoginViewState? = null

        coEvery { getUserAccountUseCase.execute() } returns Try.Failure(Try.FailureCause.UserNotLogged)
        coEvery { getAccessTokenUseCase.execute() } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.oauthViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should show unknown error when disconnection detected during oauth`() {
        var viewStatePosted: LoginViewState? = null

        coEvery { getUserAccountUseCase.execute() } returns Try.Failure(Try.FailureCause.UserNotLogged)
        coEvery { getAccessTokenUseCase.execute() } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.oauthViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should show oauth state when oauth data becomes available`() {
        var viewStatePosted: LoginViewState? = null

        val expectedUrl =
            "https://www.themoviedb.org/authenticate/aRequestToken?redirect_to=http://www.mp.com/approved"
        val expectedRedirectUrl = "http://www.mp.com/approved"

        val accessToken = AccessToken(
            success = true,
            expires_at = "anExpiration",
            request_token = "aRequestToken"
        )

        coEvery { getUserAccountUseCase.execute() } returns Try.Failure(Try.FailureCause.UserNotLogged)
        coEvery { getAccessTokenUseCase.execute() } returns Try.Success(accessToken)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)

        assertEquals(View.VISIBLE, viewStatePosted?.oauthViewState?.visibility)
        assertEquals(expectedUrl, viewStatePosted?.oauthViewState?.url)
        assertEquals(expectedRedirectUrl, viewStatePosted?.oauthViewState?.interceptUrl)
        assertEquals(false, viewStatePosted?.oauthViewState?.reminder)
    }
}
