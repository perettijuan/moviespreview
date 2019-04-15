package com.jpp.mp.screens.main.account

import androidx.lifecycle.Observer
import com.jpp.mp.utiltest.InstantTaskExecutorExtension
import com.jpp.mp.resumedLifecycleOwner
import com.jpp.mp.screens.main.TestCoroutineDispatchers
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.UserAvatar
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase
import com.jpp.mpdomain.usecase.session.CreateSessionUseCase
import com.jpp.mpdomain.usecase.session.GetAuthenticationDataUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class AccountViewModelTest {

    @MockK
    private lateinit var getAccountInfoUseCase: GetAccountInfoUseCase
    @MockK
    private lateinit var getAuthenticationDataUseCase: GetAuthenticationDataUseCase
    @MockK
    private lateinit var createSessionUseCase: CreateSessionUseCase

    private lateinit var subject: AccountViewModel

    @BeforeEach
    fun setUp() {
        subject = AccountViewModel(
                TestCoroutineDispatchers(),
                getAccountInfoUseCase,
                getAuthenticationDataUseCase,
                createSessionUseCase
        )
    }

    @Test
    fun `Should push loading and fetch Oauth data when user not logged in`() {
        val viewStatePosted = mutableListOf<AccountViewState>()
        val expectedUrl = "aUrl"
        val expectedRedirect = "aRedirect"
        val expectedToken = mockk<AccessToken>()

        every { getAccountInfoUseCase.getAccountInfo() } returns GetAccountInfoUseCase.AccountInfoResult.UserNotLoggedIn
        every { getAuthenticationDataUseCase.getAuthenticationData() } returns GetAuthenticationDataUseCase.AuthenticationDataResult.Success(expectedUrl, expectedRedirect, expectedToken)

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init()

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is AccountViewState.Loading)
        assertTrue(viewStatePosted[1] is AccountViewState.Oauth)

        with (viewStatePosted[1] as AccountViewState.Oauth) {
            assertEquals(expectedUrl, url)
            assertEquals(expectedRedirect, interceptUrl)
            assertEquals(expectedToken, accessToken)
            assertFalse(reminder)
        }

        verify(exactly = 1) { getAccountInfoUseCase.getAccountInfo() }
        verify(exactly = 1) { getAuthenticationDataUseCase.getAuthenticationData() }
    }

    @Test
    fun `Should push loading and fetch Oauth data when user not logged in and show ErrorNoConnectivity`() {
        val viewStatePosted = mutableListOf<AccountViewState>()

        every { getAccountInfoUseCase.getAccountInfo() } returns GetAccountInfoUseCase.AccountInfoResult.UserNotLoggedIn
        every { getAuthenticationDataUseCase.getAuthenticationData() } returns GetAuthenticationDataUseCase.AuthenticationDataResult.ErrorNoConnectivity

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init()

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is AccountViewState.Loading)
        assertTrue(viewStatePosted[1] is AccountViewState.ErrorNoConnectivity)

        verify(exactly = 1) { getAccountInfoUseCase.getAccountInfo() }
        verify(exactly = 1) { getAuthenticationDataUseCase.getAuthenticationData() }
    }

    @Test
    fun `Should push loading and fetch Oauth data when user not logged in and show ErrorUnknown`() {
        val viewStatePosted = mutableListOf<AccountViewState>()

        every { getAccountInfoUseCase.getAccountInfo() } returns GetAccountInfoUseCase.AccountInfoResult.UserNotLoggedIn
        every { getAuthenticationDataUseCase.getAuthenticationData() } returns GetAuthenticationDataUseCase.AuthenticationDataResult.ErrorUnknown

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init()

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is AccountViewState.Loading)
        assertTrue(viewStatePosted[1] is AccountViewState.ErrorUnknown)

        verify(exactly = 1) { getAccountInfoUseCase.getAccountInfo() }
        verify(exactly = 1) { getAuthenticationDataUseCase.getAuthenticationData() }
    }

    @Test
    fun `Should push loading and fetch account info when user logged in`() {
        val viewStatePosted = mutableListOf<AccountViewState>()
        val hash = "hashString"
        val gravatar = Gravatar(hash)
        val userAvatar = UserAvatar(gravatar)
        val expectedUserName = "aUserName"
        val expectedAccountName = "anAccountName"
        val account = UserAccount(avatar = userAvatar, name = expectedUserName, username = expectedAccountName, id = 12.toDouble())

        every { getAccountInfoUseCase.getAccountInfo() } returns GetAccountInfoUseCase.AccountInfoResult.AccountInfo(account)

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init()

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is AccountViewState.Loading)
        assertTrue(viewStatePosted[1] is AccountViewState.AccountContent)

        with ((viewStatePosted[1] as AccountViewState.AccountContent).headerItem) {
            assertEquals(hash, avatarUrl)
            assertEquals(expectedUserName, userName)
            assertEquals(expectedAccountName, accountName)
        }

        verify(exactly = 1) { getAccountInfoUseCase.getAccountInfo() }
        verify(exactly = 0) { getAuthenticationDataUseCase.getAuthenticationData() }
    }

    @Test
    fun `Should push loading and fetch account info when user logged in and show ErrorNoConnectivity`() {
        val viewStatePosted = mutableListOf<AccountViewState>()

        every { getAccountInfoUseCase.getAccountInfo() } returns GetAccountInfoUseCase.AccountInfoResult.ErrorNoConnectivity

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init()

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is AccountViewState.Loading)
        assertTrue(viewStatePosted[1] is AccountViewState.ErrorNoConnectivity)
    }

    @Test
    fun `Should push loading and fetch account info when user logged in and show ErrorUnknown`() {
        val viewStatePosted = mutableListOf<AccountViewState>()

        every { getAccountInfoUseCase.getAccountInfo() } returns GetAccountInfoUseCase.AccountInfoResult.ErrorUnknown

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init()

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is AccountViewState.Loading)
        assertTrue(viewStatePosted[1] is AccountViewState.ErrorUnknown)
    }
}