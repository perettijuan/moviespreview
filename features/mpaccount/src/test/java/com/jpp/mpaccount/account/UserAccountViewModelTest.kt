package com.jpp.mpaccount.account

import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.TestAccountCoroutineDispatchers
import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.MPConnectivityRepository
import com.jpp.mpdomain.repository.MPSessionRepository
import com.jpp.mpdomain.repository.MPUserAccountRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class UserAccountViewModelTest {

    @MockK
    private lateinit var connectivityRepository: MPConnectivityRepository
    @RelaxedMockK
    private lateinit var sessionRepository: MPSessionRepository
    @RelaxedMockK
    private lateinit var userAccountRepository: MPUserAccountRepository

    private val lvSession = MutableLiveData<MPSessionRepository.SessionData>()
    private val lvUserAccount = MutableLiveData<MPUserAccountRepository.UserAccountData>()
    private val lvConnectivity = MutableLiveData<Connectivity>()

    private lateinit var subject: UserAccountViewModel


    @BeforeEach
    fun setUp() {
        every { connectivityRepository.data() } returns lvConnectivity
        every { sessionRepository.data() } returns lvSession
        every { userAccountRepository.data() } returns lvUserAccount

        subject = UserAccountViewModel(
                TestAccountCoroutineDispatchers(),
                connectivityRepository,
                sessionRepository,
                userAccountRepository
        )

        /*
         * Since the ViewModel uses a MediatorLiveData, we need to have
         * an observer on the view states attached all the time in order
         * to get notifications.
         */
        subject.viewStates.observeForever { }
    }

    @Test
    fun `Should redirect with user not logged in`() {
        var eventPosted: UserAccountNavigationEvent? = null

        subject.navEvents.observeWith { eventPosted = it }

        lvSession.postValue(MPSessionRepository.SessionData.NoCurrentSessionAvailable)

        assertEquals(UserAccountNavigationEvent.GoToLogin, eventPosted)
    }

    @Test
    fun `Should post loading and fetch user account when user logged in`() {
        var viewStatePosted: UserAccountViewState? = null
        val session = mockk<Session>()

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvSession.postValue(MPSessionRepository.SessionData.CurrentSession(session))

        verify { userAccountRepository.getUserAccount(session) }
        assertEquals(UserAccountViewState.Loading, viewStatePosted)
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

        lvUserAccount.postValue(MPUserAccountRepository.UserAccountData.Success(userAccount))

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

        lvUserAccount.postValue(MPUserAccountRepository.UserAccountData.Success(userAccount))

        assertEquals(expected, actual)
    }

    @Test
    fun `Should post error when failing to fetch user account data`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvUserAccount.postValue(MPUserAccountRepository.UserAccountData.NoUserAccountData)

        assertEquals(UserAccountViewState.ShowError, viewStatePosted)
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvConnectivity.postValue(Connectivity.Disconnected)

        assertEquals(UserAccountViewState.NotConnected, viewStatePosted)
    }

    @Test
    fun `Should post loading and verify user logged in when reconnected`() {
        val viewStatesPosted = mutableListOf<UserAccountViewState>()

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatesPosted.add(viewState) } }

        lvConnectivity.postValue(Connectivity.Disconnected)
        lvConnectivity.postValue(Connectivity.Connected)

        verify(exactly = 1) { sessionRepository.getCurrentSession() }
        assertEquals(2, viewStatesPosted.size)
        assertEquals(UserAccountViewState.NotConnected, viewStatesPosted[0])
        assertEquals(UserAccountViewState.Loading, viewStatesPosted[1])
    }
}