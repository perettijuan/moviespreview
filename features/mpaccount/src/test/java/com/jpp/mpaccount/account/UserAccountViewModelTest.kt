package com.jpp.mpaccount.account

import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.TestAccountCoroutineDispatchers
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.UserAvatar
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class UserAccountViewModelTest {

    @RelaxedMockK
    private lateinit var accountInteractor: UserAccountInteractor

    private val lvInteractorEvents = MutableLiveData<UserAccountInteractor.UserAccountEvent>()

    private lateinit var subject: UserAccountViewModel


    @BeforeEach
    fun setUp() {
        every { accountInteractor.userAccountEvents } returns lvInteractorEvents

        subject = UserAccountViewModel(
                TestAccountCoroutineDispatchers(),
                accountInteractor
        )

        /*
         * Since the ViewModel uses a MediatorLiveData, we need to have
         * an observer on the view states attached all the time in order
         * to get notifications.
         */
        subject.viewStates.observeForever { }
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(UserAccountInteractor.UserAccountEvent.NotConnectedToNetwork)

        assertEquals(UserAccountViewState.ShowNotConnected, viewStatePosted)
    }

    @Test
    fun `Should post error when failing to fetch user account data`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(UserAccountInteractor.UserAccountEvent.UnknownError)

        assertEquals(UserAccountViewState.ShowError, viewStatePosted)
    }

    @Test
    fun `Should redirect with user not logged in`() {
        var eventPosted: UserAccountNavigationEvent? = null

        subject.navEvents.observeWith { eventPosted = it }

        lvInteractorEvents.postValue(UserAccountInteractor.UserAccountEvent.UserNotLogged)

        assertEquals(UserAccountNavigationEvent.GoToLogin, eventPosted)
    }


    @Test
    fun `Should map user account data and post data into view when user account is fetched`() {
        val userGravatar = Gravatar("someHash")
        val userAccount = UserAccount(
                avatar = UserAvatar(userGravatar),
                id = 12.toDouble(),
                name = "aName",
                username = "aUserName"
        )
        val expected = UserAccountViewState.ShowUserAccountData(
                avatarUrl = Gravatar.BASE_URL + "someHash" + Gravatar.REDIRECT,
                userName = "aName",
                accountName = "aUserName",
                defaultLetter = 'a'
        )
        var actual: UserAccountViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> actual = viewState } }

        lvInteractorEvents.postValue(UserAccountInteractor.UserAccountEvent.Success(userAccount))

        assertEquals(expected, actual)
    }

    @Test
    fun `Should map user account data - without name - and post data into view when user account is fetched`() {
        val userGravatar = Gravatar("someHash")
        val userAccount = UserAccount(
                avatar = UserAvatar(userGravatar),
                id = 12.toDouble(),
                name = "",
                username = "UserName"
        )
        val expected = UserAccountViewState.ShowUserAccountData(
                avatarUrl = Gravatar.BASE_URL + "someHash" + Gravatar.REDIRECT,
                userName = "UserName",
                accountName = "UserName",
                defaultLetter = 'U'
        )
        var actual: UserAccountViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> actual = viewState } }

        lvInteractorEvents.postValue(UserAccountInteractor.UserAccountEvent.Success(userAccount))

        assertEquals(expected, actual)
    }

    @Test
    fun `Should post loading and fetch user account onInit`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }
        subject.onInit()

        verify { accountInteractor.fetchUserAccountData() }
        assertEquals(UserAccountViewState.Loading, viewStatePosted)
    }
}