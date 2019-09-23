package com.jpp.mpaccount.account.lists

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.TestAccountCoroutineDispatchers
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

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
        subject.viewState.observeForever { }
    }

    @ParameterizedTest
    @MethodSource("userMovieListTestParams")
    fun `Should post no connectivity error when disconnected`(param: UserMovieListParam) {
        var viewStatePosted: UserMovieListViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(param)

        lvInteractorEvents.postValue(UserMovieListEvent.NotConnectedToNetwork)

        assertNotNull(viewStatePosted)
        assertEquals(param.section.titleRes, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @ParameterizedTest
    @MethodSource("userMovieListTestParams")
    fun `Should retry to fetch data when not connected and retry is executed`(param: UserMovieListParam) {
        var viewStatePosted: UserMovieListViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(param)
        lvInteractorEvents.postValue(UserMovieListEvent.NotConnectedToNetwork)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            when (param.section) {
                UserMovieListType.FAVORITE_LIST -> verify(exactly = 2) { userMovieListInteractor.fetchFavoriteMovies(any(), any()) }
                UserMovieListType.RATED_LIST -> verify(exactly = 2) { userMovieListInteractor.fetchRatedMovies(any(), any()) }
                UserMovieListType.WATCH_LIST -> verify(exactly = 2) { userMovieListInteractor.fetchWatchlist(any(), any()) }
            }
        } ?: fail()
    }

    @ParameterizedTest
    @MethodSource("userMovieListTestParams")
    fun `Should post error when failing to fetch user account data`(param: UserMovieListParam) {
        var viewStatePosted: UserMovieListViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(param)

        lvInteractorEvents.postValue(UserMovieListEvent.UnknownError)

        assertNotNull(viewStatePosted)
        assertEquals(param.section.titleRes, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @ParameterizedTest
    @MethodSource("userMovieListTestParams")
    fun `Should retry to fetch data when error unknown and retry is executed`(param: UserMovieListParam) {
        var viewStatePosted: UserMovieListViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(param)

        lvInteractorEvents.postValue(UserMovieListEvent.UnknownError)

        assertEquals(param.section.titleRes, viewStatePosted?.screenTitle)
        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            when (param.section) {
                UserMovieListType.FAVORITE_LIST -> verify(exactly = 2) { userMovieListInteractor.fetchFavoriteMovies(any(), any()) }
                UserMovieListType.RATED_LIST -> verify(exactly = 2) { userMovieListInteractor.fetchRatedMovies(any(), any()) }
                UserMovieListType.WATCH_LIST -> verify(exactly = 2) { userMovieListInteractor.fetchWatchlist(any(), any()) }
            }
        } ?: fail()
    }

    @Test
    fun `Should redirect when user not logged in`() {
        var eventPosted: UserMovieListNavigationEvent? = null

        subject.navEvents.observeWith { it.actionIfNotHandled { naveEvent -> eventPosted = naveEvent } }

        lvInteractorEvents.postValue(UserMovieListEvent.UserNotLogged)

        assertEquals(UserMovieListNavigationEvent.GoToUserAccount, eventPosted)
    }

    @ParameterizedTest
    @MethodSource("userMovieListTestParams")
    fun `Should fetch movies, adapt result to UI and post value`(param: UserMovieListParam) {
        var viewStatePosted: UserMovieListViewState? = null
        val mockedList = getMockedMovies()
        val slot = slot<(List<Movie>) -> Unit>()

        when (param.section) {
            UserMovieListType.FAVORITE_LIST -> every { userMovieListInteractor.fetchFavoriteMovies(any(), capture(slot)) } answers { slot.captured.invoke(mockedList) }
            UserMovieListType.RATED_LIST -> every { userMovieListInteractor.fetchRatedMovies(any(), capture(slot)) } answers { slot.captured.invoke(mockedList) }
            UserMovieListType.WATCH_LIST -> every { userMovieListInteractor.fetchWatchlist(any(), capture(slot)) } answers { slot.captured.invoke(mockedList) }
        }
        every { imagesPathInteractor.configurePathMovie(any(), any(), any()) } answers { arg(2) }

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(param)

        assertNotNull(viewStatePosted)
        assertEquals(param.section.titleRes, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(mockedList.size, viewStatePosted?.contentViewState?.movieList?.size)


        when (param.section) {
            UserMovieListType.FAVORITE_LIST -> verify { userMovieListInteractor.fetchFavoriteMovies(any(), any()) }
            UserMovieListType.RATED_LIST -> verify { userMovieListInteractor.fetchRatedMovies(any(), any()) }
            UserMovieListType.WATCH_LIST -> verify { userMovieListInteractor.fetchWatchlist(any(), any()) }
        }
        verify(exactly = mockedList.size) { imagesPathInteractor.configurePathMovie(10, 10, any()) }
    }

    private fun getMockedMovies(): List<Movie> {
        return mutableListOf<Movie>().apply {
            for (i in 0..50) {
                add(
                        Movie(
                                id = i.toDouble(),
                                title = "titleRes$i",
                                original_language = "oTitle$i",
                                overview = "overview$i",
                                release_date = "releaseDate$i",
                                original_title = "originalLanguage$i",
                                poster_path = "posterPath$i",
                                backdrop_path = "backdropPath$i",
                                vote_count = i.toDouble(),
                                vote_average = i.toFloat(),
                                popularity = i.toFloat()
                        )
                )
            }
        }
    }


    companion object {

        @JvmStatic
        fun userMovieListTestParams() = listOf(
                Arguments.arguments(UserMovieListParam(UserMovieListType.FAVORITE_LIST, 10, 10)),
                Arguments.arguments(UserMovieListParam(UserMovieListType.RATED_LIST, 10, 10)),
                Arguments.arguments(UserMovieListParam(UserMovieListType.WATCH_LIST, 10, 10))
        )
    }
}