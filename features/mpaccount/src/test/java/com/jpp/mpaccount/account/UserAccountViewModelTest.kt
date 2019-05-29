package com.jpp.mpaccount.account

import androidx.lifecycle.MutableLiveData
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
import org.junit.jupiter.api.Assertions.assertTrue
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
                defaultLetter = 'a',
                favoriteMovieState = UserMoviesViewState.ShowError,
                ratedMovieState = UserMoviesViewState.ShowError,
                watchListState = UserMoviesViewState.ShowError
        )
        var actual: UserAccountViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> actual = viewState } }

        lvInteractorEvents.postValue(
                UserAccountInteractor.UserAccountEvent.Success(
                        userAccount,
                        UserAccountInteractor.UserMoviesState.UnknownError,
                        UserAccountInteractor.UserMoviesState.UnknownError,
                        UserAccountInteractor.UserMoviesState.UnknownError)
        )

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
                defaultLetter = 'U',
                favoriteMovieState = UserMoviesViewState.ShowError,
                ratedMovieState = UserMoviesViewState.ShowError,
                watchListState = UserMoviesViewState.ShowError
        )
        var actual: UserAccountViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> actual = viewState } }

        lvInteractorEvents.postValue(
                UserAccountInteractor.UserAccountEvent.Success(
                        userAccount,
                        UserAccountInteractor.UserMoviesState.UnknownError,
                        UserAccountInteractor.UserMoviesState.UnknownError,
                        UserAccountInteractor.UserMoviesState.UnknownError)
        )

        assertEquals(expected, actual)
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
        var actual: UserAccountViewState? = null
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

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> actual = viewState } }

        lvInteractorEvents.postValue(
                UserAccountInteractor.UserAccountEvent.Success(
                        userAccount,
                        UserAccountInteractor.UserMoviesState.Success(favMoviePage),
                        UserAccountInteractor.UserMoviesState.Success(ratedMoviePage),
                        UserAccountInteractor.UserMoviesState.Success(watchListMoviePage)
                )
        )

        assertTrue(actual is UserAccountViewState.ShowUserAccountData)
        with(actual as UserAccountViewState.ShowUserAccountData) {
            assertTrue(this.favoriteMovieState is UserMoviesViewState.ShowUserMovies)
            with(this.favoriteMovieState as UserMoviesViewState.ShowUserMovies) {
                assertEquals(5, this.items.size)
            }

            assertTrue(this.ratedMovieState is UserMoviesViewState.ShowUserMovies)
            with(this.ratedMovieState as UserMoviesViewState.ShowUserMovies) {
                assertEquals(4, this.items.size)
            }

            assertTrue(this.watchListState is UserMoviesViewState.ShowUserMovies)
            with(this.watchListState as UserMoviesViewState.ShowUserMovies) {
                assertEquals(3, this.items.size)
            }
        }
        verify(exactly = 12) { imagesPathInteractor.configurePathMovie(any(), any(), any()) }
    }

    @Test
    fun `Should post loading and fetch user account onInit`() {
        var viewStatePosted: UserAccountViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }
        subject.onInit(10)

        verify { accountInteractor.fetchUserAccountData() }
        assertEquals(UserAccountViewState.Loading, viewStatePosted)
    }
}