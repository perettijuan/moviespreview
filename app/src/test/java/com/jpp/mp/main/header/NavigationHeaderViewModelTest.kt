package com.jpp.mp.main.header

import android.view.View
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.R
import com.jpp.mp.common.navigation.Destination
import com.jpp.mp.main.TestCoroutineDispatchers
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class NavigationHeaderViewModelTest {

    @RelaxedMockK
    private lateinit var interactor: NavigationHeaderInteractor

    private lateinit var subject: NavigationHeaderViewModel

    private val interactorEvents = MediatorLiveData<NavigationHeaderInteractor.HeaderDataEvent>()

    @BeforeEach
    fun setUp() {
        every { interactor.userAccountEvents } returns interactorEvents
        subject = NavigationHeaderViewModel(TestCoroutineDispatchers(), interactor)
    }

    @Test
    fun `Should post login state when user not logged`() {
        var viewStatePosted: HeaderViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        interactorEvents.postValue(NavigationHeaderInteractor.HeaderDataEvent.UserNotLogged)

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

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        interactorEvents.postValue(NavigationHeaderInteractor.HeaderDataEvent.UserNotLogged)

        interactorEvents.postValue(NavigationHeaderInteractor.HeaderDataEvent.UnknownError)

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

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        interactorEvents.postValue(NavigationHeaderInteractor.HeaderDataEvent.Success(userAccount))

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

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        interactorEvents.postValue(NavigationHeaderInteractor.HeaderDataEvent.Success(userAccount))

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
    fun `Should post loading and get account info in onInit`() {
        var viewStatePosted: HeaderViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.VISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.GONE, viewStatePosted?.accountViewState?.visibility)
        assertEquals(View.GONE, viewStatePosted?.detailsViewState?.visibility)

        assertEquals(View.GONE, viewStatePosted?.loginButtonViewState?.visibility)

        verify { interactor.getUserAccountData() }
    }

    @Test
    fun `Should request navigation to account details when onNavigateToLoginSelected`() {
        val expectedDestination = Destination.MPAccount

        var requestedDestination: Destination? = null

        subject.navigationEvents.observeWith { it.actionIfNotHandled { dest -> requestedDestination = dest } }
        subject.onNavigateToLoginSelected()

        assertEquals(expectedDestination, requestedDestination)
    }

    @Test
    fun `Should request navigation to account details when onNavigateToAccountDetailsSelected`() {
        val expectedDestination = Destination.MPAccount

        var requestedDestination: Destination? = null

        subject.navigationEvents.observeWith { it.actionIfNotHandled { dest -> requestedDestination = dest } }
        subject.onNavigateToAccountDetailsSelected()

        assertEquals(expectedDestination, requestedDestination)
    }
}
