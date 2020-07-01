package com.jpp.mp.main.header

import android.view.View
import com.jpp.mp.R
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.UserAvatar
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
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
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
class NavigationHeaderViewModelTest {

    @RelaxedMockK
    private lateinit var getUserAccountUseCase: GetUserAccountUseCase

    @RelaxedMockK
    private lateinit var navigator: HeaderNavigator

    @RelaxedMockK
    private lateinit var sessionRepository: SessionRepository

    private val sessionRepositoryUpdates: BroadcastChannel<Session?> = BroadcastChannel(Channel.CONFLATED)

    private lateinit var subject: NavigationHeaderViewModel

    @BeforeEach
    fun setUp() {
        coEvery { sessionRepository.sessionStateUpdates() } returns sessionRepositoryUpdates
        subject = NavigationHeaderViewModel(
            getUserAccountUseCase,
            navigator,
            sessionRepository,
            CoroutineTestExtension.testDispatcher
        )
    }

    @Test
    fun `Should post login state when user not logged`() {
        var viewStatePosted: HeaderViewState? = null

        coEvery { getUserAccountUseCase.execute() } returns Try.Failure(Try.FailureCause.UserNotLogged)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.GONE, viewStatePosted?.accountViewState?.visibility)
        assertEquals(View.GONE, viewStatePosted?.detailsViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.loginButtonViewState?.visibility)
        assertEquals(R.string.nav_header_login, viewStatePosted?.loginButtonViewState?.title)
    }

    @Test
    fun `Should post login state when error detected`() {
        var viewStatePosted: HeaderViewState? = null

        coEvery { getUserAccountUseCase.execute() } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.GONE, viewStatePosted?.accountViewState?.visibility)
        assertEquals(View.GONE, viewStatePosted?.detailsViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.loginButtonViewState?.visibility)
        assertEquals(R.string.nav_header_login, viewStatePosted?.loginButtonViewState?.title)
    }

    @Test
    fun `Should post account data with avatar when retrieved`() {
        var viewStatePosted: HeaderViewState? = null
        val userAccount = UserAccount(
                avatar = UserAvatar(Gravatar(hash = "anUrl")),
                id = 12.toDouble(),
                name = "aName",
                username = "anAccount"
        )

        coEvery { getUserAccountUseCase.execute() } returns Try.Success(userAccount)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.GONE, viewStatePosted?.loginButtonViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.accountViewState?.visibility)
        assertEquals("aName", viewStatePosted?.accountViewState?.userName)
        assertEquals("anAccount", viewStatePosted?.accountViewState?.accountName)
        assertEquals(Gravatar.BASE_URL + "anUrl" + Gravatar.REDIRECT, viewStatePosted?.accountViewState?.avatarViewState?.avatarUrl)
        assertEquals(View.VISIBLE, viewStatePosted?.accountViewState?.avatarViewState?.avatarVisibility)
        assertEquals(View.GONE, viewStatePosted?.accountViewState?.avatarViewState?.defaultLetterVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.detailsViewState?.visibility)
    }

    @Test
    fun `Should post account data with default letter when fails to download image`() {
        var viewStatePosted: HeaderViewState? = null
        val userAccount = UserAccount(
                avatar = UserAvatar(Gravatar(hash = "anUrl")),
                id = 12.toDouble(),
                name = "aName",
                username = "anAccount"
        )

        coEvery { getUserAccountUseCase.execute() } returns Try.Success(userAccount)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        viewStatePosted?.let {
            viewStatePosted?.accountViewState?.avatarViewState?.avatarErrorCallback?.invoke()
        } ?: fail()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.GONE, viewStatePosted?.loginButtonViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.accountViewState?.visibility)
        assertEquals("aName", viewStatePosted?.accountViewState?.userName)
        assertEquals("anAccount", viewStatePosted?.accountViewState?.accountName)
        assertNull(viewStatePosted?.accountViewState?.avatarViewState?.avatarUrl)
        assertEquals(View.INVISIBLE, viewStatePosted?.accountViewState?.avatarViewState?.avatarVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.accountViewState?.avatarViewState?.defaultLetterVisibility)
        assertEquals("A", viewStatePosted?.accountViewState?.avatarViewState?.defaultLetter)
        assertEquals(View.VISIBLE, viewStatePosted?.detailsViewState?.visibility)
    }

    @Test
    fun `Should refresh data when user logs out`() = runBlocking {
        sessionRepositoryUpdates.send(null)
        coVerify { getUserAccountUseCase.execute() }
    }

    @Test
    fun `Should refresh data when user logs in`() = runBlocking {
        sessionRepositoryUpdates.send(mockk())
        coVerify { getUserAccountUseCase.execute() }
    }
}
