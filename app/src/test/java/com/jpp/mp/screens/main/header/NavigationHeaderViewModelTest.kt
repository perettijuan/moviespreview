package com.jpp.mp.screens.main.header

import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.screens.main.TestCoroutineDispatchers
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.UserAvatar
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class NavigationHeaderViewModelTest {

    @RelaxedMockK
    private lateinit var interactor: NavigationHeaderInteractor

    private lateinit var subject: NavigationHeaderViewModel

    private val interactorEvents by lazy { MediatorLiveData<NavigationHeaderInteractor.HeaderDataEvent>() }

    @BeforeEach
    fun setUp() {
        every { interactor.userAccountEvents } returns interactorEvents
        subject = NavigationHeaderViewModel(TestCoroutineDispatchers(), interactor)
    }

    @Test
    fun `Should post login state when user not logged`() {
        var postedViewState: HeaderViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> postedViewState = viewState } }

        interactorEvents.postValue(NavigationHeaderInteractor.HeaderDataEvent.UserNotLogged)

        assertEquals(postedViewState, HeaderViewState.ShowLogin)
    }

    @Test
    fun `Should post login state when error detected`() {
        var postedViewState: HeaderViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> postedViewState = viewState } }

        interactorEvents.postValue(NavigationHeaderInteractor.HeaderDataEvent.UnknownError)

        assertEquals(postedViewState, HeaderViewState.ShowLogin)
    }

    @Test
    fun `Should post account data when retrieved`() {
        var postedViewState: HeaderViewState? = null
        val userAccount = UserAccount(
                avatar = UserAvatar(Gravatar(hash = "anUrl")),
                id = 12.toDouble(),
                name = "aName",
                username = "anAccount"
        )

        val expected = HeaderViewState.ShowAccount(
                avatarUrl = Gravatar.BASE_URL + "anUrl" + Gravatar.REDIRECT,
                userName = "aName",
                defaultLetter = 'a',
                accountName = "anAccount"
        )


        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> postedViewState = viewState } }

        interactorEvents.postValue(NavigationHeaderInteractor.HeaderDataEvent.Success(userAccount))

        assertEquals(expected, postedViewState)
    }

    @Test
    fun `Should post account data with username when retrieved`() {
        var postedViewState: HeaderViewState? = null
        val userAccount = UserAccount(
                avatar = UserAvatar(Gravatar(hash = "anUrl")),
                id = 12.toDouble(),
                name = "",
                username = "anAccount"
        )

        val expected = HeaderViewState.ShowAccount(
                avatarUrl = Gravatar.BASE_URL + "anUrl" + Gravatar.REDIRECT,
                userName = "anAccount",
                defaultLetter = 'a',
                accountName = "anAccount"
        )


        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> postedViewState = viewState } }

        interactorEvents.postValue(NavigationHeaderInteractor.HeaderDataEvent.Success(userAccount))

        assertEquals(expected, postedViewState)
    }

    @Test
    fun `Should post loading and get account info in onInit`() {
        var postedViewState: HeaderViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> postedViewState = viewState } }

        subject.onInit()

        assertEquals(postedViewState, HeaderViewState.ShowLoading)
        verify { interactor.fetchUserData() }
    }
}