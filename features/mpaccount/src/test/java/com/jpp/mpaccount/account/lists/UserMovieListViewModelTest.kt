package com.jpp.mpaccount.account.lists

import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.TestAccountCoroutineDispatchers
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent
import com.jpp.mpdomain.interactors.ImagesPathInteractor
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
class UserMovieListViewModelTest {

    @RelaxedMockK
    private lateinit var userMovieListInteractor: UserMovieListInteractor
    @MockK
    private lateinit var imagesPathInteractor: ImagesPathInteractor

    private val lvInteractorEvents = MutableLiveData<UserMovieListEvent>()

    private lateinit var subject: UserMovieListViewModel

    @BeforeEach
    fun setUp() {
        every { userMovieListInteractor.userAccountEvents } returns lvInteractorEvents

        subject = UserMovieListViewModel(
                TestAccountCoroutineDispatchers(),
                userMovieListInteractor,
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
        var viewStatePosted: UserMovieListViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(UserMovieListEvent.NotConnectedToNetwork)

        assertEquals(UserMovieListViewState.ShowNotConnected, viewStatePosted)
    }

    @Test
    fun `Should post error when failing to fetch user account data`() {
        var viewStatePosted: UserMovieListViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(UserMovieListEvent.UnknownError)

        assertEquals(UserMovieListViewState.ShowError, viewStatePosted)
    }

    @Test
    fun `Should redirect when user not logged in`() {
        var eventPosted: UserMovieListNavigationEvent? = null

        subject.navEvents.observeWith { eventPosted = it }

        lvInteractorEvents.postValue(UserMovieListEvent.UserNotLogged)

        assertEquals(UserMovieListNavigationEvent.GoToUserAccount, eventPosted)
    }

    @Test
    fun `Should post loading and create paged list with first movie pages onInitWithFavorites`() {
        val viewStatesPosted = mutableListOf<UserMovieListViewState>()


        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatesPosted.add(viewState) } }

        subject.onInitWithFavorites(10, 10)

        assertEquals(UserMovieListViewState.ShowLoading, viewStatesPosted[0])
        assertTrue(viewStatesPosted[1] is UserMovieListViewState.ShowMovieList)
        verify { userMovieListInteractor.fetchFavoriteMovies(1, any()) }
    }
}