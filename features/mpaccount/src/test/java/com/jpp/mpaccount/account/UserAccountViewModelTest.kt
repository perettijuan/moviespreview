package com.jpp.mpaccount.account

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpaccount.R
import com.jpp.mpaccount.TestAccountCoroutineDispatchers
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.UserAvatar
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class UserAccountViewModelTest {

    @RelaxedMockK
    private lateinit var accountInteractor: UserAccountInteractor
    @RelaxedMockK
    private lateinit var imagesPathInteractor: ImagesPathInteractor

    private val lvInteractorEvents = MutableLiveData<UserAccountInteractor.UserAccountEvent>()

    private lateinit var subject: UserAccountViewModel

    @BeforeEach
    fun setUp() {
        every { accountInteractor.userAccountEvents } returns lvInteractorEvents

        subject = UserAccountViewModel(
                TestAccountCoroutineDispatchers(),
                accountInteractor,
                imagesPathInteractor
        )

        /*
         * Since the ViewModel uses a MediatorLiveData, we need to have
         * an observer on the view states attached all the time in order
         * to get notifications.
         */
        subject.viewState.observeForever { }
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvInteractorEvents.postValue(UserAccountInteractor.UserAccountEvent.NotConnectedToNetwork)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should post error when failing to fetch user account data`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvInteractorEvents.postValue(UserAccountInteractor.UserAccountEvent.UnknownError)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should redirect when user not logged in`() {
        val expectedDestination = Destination.PreviousDestination
        var requestedDestination: Destination? = null

        subject.navigationEvents.observeWith { it.actionIfNotHandled { dest -> requestedDestination = dest } }

        lvInteractorEvents.postValue(UserAccountInteractor.UserAccountEvent.UserNotLogged)

        assertEquals(expectedDestination, requestedDestination)
    }

    @Test
    fun `Should redirect to main when user data cleared`() {
        val expectedDestination = Destination.PreviousDestination
        var requestedDestination: Destination? = null

        subject.navigationEvents.observeWith { it.actionIfNotHandled { dest -> requestedDestination = dest } }

        lvInteractorEvents.postValue(UserAccountInteractor.UserAccountEvent.UserDataCleared)

        assertEquals(expectedDestination, requestedDestination)
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
        var viewStatePosted: UserAccountViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvInteractorEvents.postValue(
                UserAccountInteractor.UserAccountEvent.Success(
                        userAccount,
                        UserAccountInteractor.UserMoviesState.UnknownError,
                        UserAccountInteractor.UserMoviesState.UnknownError,
                        UserAccountInteractor.UserMoviesState.UnknownError)
        )

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals("aName", viewStatePosted?.contentViewState?.userName)
        assertEquals("aUserName", viewStatePosted?.contentViewState?.accountName)
        assertEquals(Gravatar.BASE_URL + "someHash" + Gravatar.REDIRECT, viewStatePosted?.contentViewState?.avatarViewState?.avatarUrl)
        assertEquals(R.string.user_account_favorite_movies_error, viewStatePosted?.contentViewState?.favoriteMovieState?.errorText)
        assertEquals(R.string.user_account_favorite_movies_error, viewStatePosted?.contentViewState?.ratedMovieState?.errorText)
        assertEquals(R.string.user_account_favorite_movies_error, viewStatePosted?.contentViewState?.watchListState?.errorText)
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

        var viewStatePosted: UserAccountViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvInteractorEvents.postValue(
                UserAccountInteractor.UserAccountEvent.Success(
                        userAccount,
                        UserAccountInteractor.UserMoviesState.UnknownError,
                        UserAccountInteractor.UserMoviesState.UnknownError,
                        UserAccountInteractor.UserMoviesState.UnknownError)
        )

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals("UserName", viewStatePosted?.contentViewState?.userName)
        assertEquals("UserName", viewStatePosted?.contentViewState?.accountName)
        assertEquals(Gravatar.BASE_URL + "someHash" + Gravatar.REDIRECT, viewStatePosted?.contentViewState?.avatarViewState?.avatarUrl)
        assertEquals(R.string.user_account_favorite_movies_error, viewStatePosted?.contentViewState?.favoriteMovieState?.errorText)
        assertEquals(R.string.user_account_favorite_movies_error, viewStatePosted?.contentViewState?.ratedMovieState?.errorText)
        assertEquals(R.string.user_account_favorite_movies_error, viewStatePosted?.contentViewState?.watchListState?.errorText)
    }

    @Test
    fun `Should map user movies`() {
        val userGravatar = Gravatar("someHash")
        val userAccount = UserAccount(
                avatar = UserAvatar(userGravatar),
                id = 12.toDouble(),
                name = "",
                username = "UserName"
        )
        var viewStatePosted: UserAccountViewState? = null
        val favMoviePage = MoviePage(
                page = 1,
                results = mutableListOf(mockk(), mockk(), mockk(), mockk(), mockk()),
                total_pages = 10,
                total_results = 100
        )

        val ratedMoviePage = MoviePage(
                page = 1,
                results = mutableListOf(mockk(), mockk(), mockk(), mockk()),
                total_pages = 10,
                total_results = 100
        )

        val watchListMoviePage = MoviePage(
                page = 1,
                results = mutableListOf(mockk(), mockk(), mockk()),
                total_pages = 10,
                total_results = 100
        )

        every { imagesPathInteractor.configurePathMovie(any(), any(), any()) } returns mockk(relaxed = true)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(UserAccountParam("aTitle", 10))

        lvInteractorEvents.postValue(
                UserAccountInteractor.UserAccountEvent.Success(
                        userAccount,
                        UserAccountInteractor.UserMoviesState.Success(favMoviePage),
                        UserAccountInteractor.UserMoviesState.Success(ratedMoviePage),
                        UserAccountInteractor.UserMoviesState.Success(watchListMoviePage)
                )
        )

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(5, viewStatePosted?.contentViewState?.favoriteMovieState?.items?.size)
        assertEquals(4, viewStatePosted?.contentViewState?.ratedMovieState?.items?.size)
        assertEquals(3, viewStatePosted?.contentViewState?.watchListState?.items?.size)

        verify(exactly = 12) { imagesPathInteractor.configurePathMovie(any(), any(), any()) }
    }

    @Test
    fun `Should post loading and fetch user account onInit`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(UserAccountParam("aTitle", 10))

        assertNotNull(viewStatePosted)
        assertEquals(View.VISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        verify { accountInteractor.fetchUserAccountData() }
    }

    @Test
    fun `Should post loading and clear user data onLogout`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onLogout()

        assertNotNull(viewStatePosted)
        assertEquals(View.VISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        verify { accountInteractor.clearUserAccountData() }
    }

    @Test
    fun `Should post loading and refresh user data when language changed`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        lvInteractorEvents.postValue(UserAccountInteractor.UserAccountEvent.UserChangedLanguage)

        assertNotNull(viewStatePosted)
        assertEquals(View.VISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        verify { accountInteractor.refreshUserAccountData() }
    }

    @Test
    fun `Should update reached destination in onInit`() {
        var destinationReached: Destination? = null
        val expected = Destination.ReachedDestination("aTitle")

        subject.destinationEvents.observeWith { destinationReached = it }

        subject.onInit(UserAccountParam("aTitle", 10))

        assertEquals(expected, destinationReached)
    }
}
