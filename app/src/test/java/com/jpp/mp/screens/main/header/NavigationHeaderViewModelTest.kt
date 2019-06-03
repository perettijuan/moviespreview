package com.jpp.mp.screens.main.header

import com.jpp.mp.screens.main.TestCoroutineDispatchers
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.UserAvatar
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class NavigationHeaderViewModelTest {

    @MockK
    private lateinit var getAccountInfoUseCase: GetAccountInfoUseCase

    private lateinit var subject: NavigationHeaderViewModel

    @BeforeEach
    fun setUp() {
        subject = NavigationHeaderViewModel(TestCoroutineDispatchers(), getAccountInfoUseCase)
    }

    @Test
    fun `Should execute use case on init, adapt result and show WithInfo view state`() {
        val viewStatePosted = mutableListOf<HeaderViewState>()
        val expectedImage = "anUrl"
        val expectedName = "aName"
        val expectedAccount = "anAccount"
        val userAccount = UserAccount(
                avatar = UserAvatar(Gravatar(hash = expectedImage)),
                id = 12.toDouble(),
                name = expectedName,
                username = expectedAccount
        )

        every { getAccountInfoUseCase.getAccountInfo() } returns GetAccountInfoUseCase.AccountInfoResult.AccountInfo(userAccount)

        subject.viewState().observeWith { viewStatePosted.add(it) }

        subject.init()

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is HeaderViewState.ShowLoading)
        assertTrue(viewStatePosted[1] is HeaderViewState.ShowAccountInfo)

        val headerAccountInfo = (viewStatePosted[1] as HeaderViewState.ShowAccountInfo).accountInfo

        // domain object mapped correctly
        assertEquals(expectedImage, headerAccountInfo.avatarUrl)
        assertEquals(expectedName, headerAccountInfo.userName)
        assertEquals(expectedAccount, headerAccountInfo.accountName)
    }


    @Test
    fun `Should execute use case on init and show Login state when ErrorNoConnectivity`() {
        val viewStatePosted = mutableListOf<HeaderViewState>()

        every { getAccountInfoUseCase.getAccountInfo() } returns GetAccountInfoUseCase.AccountInfoResult.ErrorNoConnectivity

        subject.viewState().observeWith { viewStatePosted.add(it) }

        subject.init()

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is HeaderViewState.ShowLoading)
        assertTrue(viewStatePosted[1] is HeaderViewState.ShowLogin)
    }


    @Test
    fun `Should execute use case on init and show Login state when ErrorUnknown`() {
        val viewStatePosted = mutableListOf<HeaderViewState>()

        every { getAccountInfoUseCase.getAccountInfo() } returns GetAccountInfoUseCase.AccountInfoResult.ErrorUnknown

        subject.viewState().observeWith { viewStatePosted.add(it) }

        subject.init()

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is HeaderViewState.ShowLoading)
        assertTrue(viewStatePosted[1] is HeaderViewState.ShowLogin)
    }

    @Test
    fun `Should execute use case on init and show Login state when UserNotLoggedIn`() {
        val viewStatePosted = mutableListOf<HeaderViewState>()

        every { getAccountInfoUseCase.getAccountInfo() } returns GetAccountInfoUseCase.AccountInfoResult.UserNotLoggedIn

        subject.viewState().observeWith { viewStatePosted.add(it) }

        subject.init()

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is HeaderViewState.ShowLoading)
        assertTrue(viewStatePosted[1] is HeaderViewState.ShowLogin)
    }
}