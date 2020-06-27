package com.jpp.mpaccount.account

import android.view.View
import com.jpp.mpaccount.R
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.UserAvatar
import com.jpp.mpdomain.usecase.GetUserAccountMoviesUseCase
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
import com.jpp.mpdomain.usecase.LogOutUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class,
    CoroutineTestExtension::class
)
class UserAccountViewModelTest {

    @RelaxedMockK
    private lateinit var getUserAccountUseCase: GetUserAccountUseCase

    @RelaxedMockK
    private lateinit var getMoviesUseCase: GetUserAccountMoviesUseCase

    @RelaxedMockK
    private lateinit var logOutUseCase: LogOutUseCase

    @RelaxedMockK
    private lateinit var userAccountNavigator: UserAccountNavigator

    private lateinit var subject: UserAccountViewModel

    @BeforeEach
    fun setUp() {
        subject = UserAccountViewModel(
            getUserAccountUseCase,
            getMoviesUseCase,
            logOutUseCase,
            userAccountNavigator,
            CoroutineTestExtension.testDispatcher
        )
    }

    @Test
    fun `Should hide header when error detected`() {
        var viewStatePosted: UserAccountHeaderState? = null

        coEvery { getUserAccountUseCase.execute() } returns Try.Failure(Try.FailureCause.Unknown)
        subject.headerViewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(R.string.account_title, viewStatePosted?.screenTitle)
        assertEquals(View.GONE, viewStatePosted?.visibility)
        assertEquals("", viewStatePosted?.userName)
        assertEquals("", viewStatePosted?.accountName)
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
        var viewStatePosted: UserAccountHeaderState? = null

        coEvery { getUserAccountUseCase.execute() } returns Try.Success(userAccount)
        subject.headerViewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.VISIBLE, viewStatePosted?.visibility)
        assertEquals("aName", viewStatePosted?.userName)
        assertEquals("aUserName", viewStatePosted?.accountName)
        assertEquals(
            Gravatar.BASE_URL + "someHash" + Gravatar.REDIRECT,
            viewStatePosted?.avatarViewState?.avatarUrl
        )
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

        var viewStatePosted: UserAccountHeaderState? = null

        coEvery { getUserAccountUseCase.execute() } returns Try.Success(userAccount)
        subject.headerViewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.VISIBLE, viewStatePosted?.visibility)
        assertEquals("UserName", viewStatePosted?.userName)
        assertEquals("UserName", viewStatePosted?.accountName)
        assertEquals(
            Gravatar.BASE_URL + "someHash" + Gravatar.REDIRECT,
            viewStatePosted?.avatarViewState?.avatarUrl
        )
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: UserAccountBodyViewState? = null

        coEvery { getMoviesUseCase.execute(any()) } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.bodyViewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.favoriteMovieState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.ratedMovieState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.watchListState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should post error when unknown error is reported`() {
        var viewStatePosted: UserAccountBodyViewState? = null

        coEvery { getMoviesUseCase.execute(any()) } returns Try.Failure(Try.FailureCause.Unknown)

        subject.bodyViewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.favoriteMovieState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.ratedMovieState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.watchListState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should redirect when user not logged in`() {
        coEvery { getMoviesUseCase.execute(any()) } returns Try.Failure(Try.FailureCause.UserNotLogged)
        subject.onInit()
        userAccountNavigator.navigateHome()
    }
}
