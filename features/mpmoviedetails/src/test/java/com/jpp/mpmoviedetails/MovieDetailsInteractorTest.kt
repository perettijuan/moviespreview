package com.jpp.mpmoviedetails

import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.*
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent
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

@ExtendWith(
        MockKExtension::class,
        InstantTaskExecutorExtension::class
)
class MovieDetailsInteractorTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @MockK
    private lateinit var movieDetailRepository: MovieDetailRepository
    @MockK
    private lateinit var languageRepository: LanguageRepository
    @MockK
    private lateinit var sessionRepository: SessionRepository
    @MockK
    private lateinit var accountRepository: AccountRepository
    @MockK
    private lateinit var movieStateRepository: MovieStateRepository
    @RelaxedMockK
    private lateinit var moviePageRepository: MoviePageRepository

    private val languageUpdates = MutableLiveData<SupportedLanguage>()

    private lateinit var subject: MovieDetailsInteractor

    @BeforeEach
    fun setUp() {
        every { languageRepository.updates() } returns languageUpdates
        subject = MovieDetailsInteractor(
                connectivityRepository,
                movieDetailRepository,
                languageRepository,
                sessionRepository,
                accountRepository,
                movieStateRepository,
                moviePageRepository)
    }

    @Test
    fun `Should post not connected to network event when fetchMovieState not connected`() {
        var eventPosted: MovieStateEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.fetchMovieState(12.0)
        assertEquals(MovieStateEvent.NotConnectedToNetwork, eventPosted)
        verify(exactly = 0) { movieStateRepository.getStateForMovie(any(), any()) }
    }

    @Test
    fun `Should post none data when connected to network but user not logged in`() {
        var eventPosted: MovieStateEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns null

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.fetchMovieState(12.0)
        assertEquals(MovieStateEvent.NoStateFound, eventPosted)
        verify(exactly = 0) { movieStateRepository.getStateForMovie(any(), any()) }
    }

    @Test
    fun `Should post unknown error when repository fails to fetch data`() {
        var eventPosted: MovieStateEvent? = null
        val session = mockk<Session>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { movieStateRepository.getStateForMovie(any(), any()) } returns null

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.fetchMovieState(12.0)
        assertEquals(MovieStateEvent.UnknownError, eventPosted)
        verify { movieStateRepository.getStateForMovie(12.0, session) }
    }

    @Test
    fun `Should post success when obtains a new movie state`() {
        var eventPosted: MovieStateEvent? = null
        val session = mockk<Session>()
        val movieState = mockk<MovieState>()
        val expected = MovieStateEvent.FetchSuccess(movieState)

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { movieStateRepository.getStateForMovie(any(), any()) } returns movieState

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.fetchMovieState(12.0)
        assertEquals(expected, eventPosted)
        verify { movieStateRepository.getStateForMovie(12.0, session) }
    }

    @Test
    fun `Should post user not logged when there is no session created on updateFavoriteMovieState`() {
        var eventPosted: MovieStateEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns null

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.updateFavoriteMovieState(12.0, true)
        assertEquals(MovieStateEvent.UserNotLogged, eventPosted)
        verify(exactly = 0) { movieStateRepository.updateFavoriteMovieState(any(), any(), any(), any()) }
    }

    @Test
    fun `Should post user not logged when there is a session created but no user account data on updateFavoriteMovieState`() {
        var eventPosted: MovieStateEvent? = null
        val session = mockk<Session>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns null

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.updateFavoriteMovieState(12.0, true)
        assertEquals(MovieStateEvent.UserNotLogged, eventPosted)
        verify(exactly = 0) { movieStateRepository.updateFavoriteMovieState(any(), any(), any(), any()) }
    }

    @Test
    fun `Should update favorite state and post result when session and account data available updateFavoriteMovieState`() {
        var eventPosted: MovieStateEvent? = null
        val session = mockk<Session>()
        val userAccount = mockk<UserAccount>()
        val expected = MovieStateEvent.UpdateFavorite(true)

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns userAccount
        every { movieStateRepository.updateFavoriteMovieState(any(), any(), any(), any()) } returns true

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.updateFavoriteMovieState(12.0, true)
        assertEquals(expected, eventPosted)
        verify { movieStateRepository.updateFavoriteMovieState(12.0, true, userAccount, session) }
        verify { moviePageRepository.flushFavoriteMoviePages() }
    }

    @Test
    fun `Should post user not logged when there is no session created on updateWatchlistMovieState`() {
        var eventPosted: MovieStateEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns null

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.updateWatchlistMovieState(12.0, true)
        assertEquals(MovieStateEvent.UserNotLogged, eventPosted)
        verify(exactly = 0) { movieStateRepository.updateWatchlistMovieState(any(), any(), any(), any()) }
    }

    @Test
    fun `Should post user not logged when there is a session created but no user account data on updateWatchlistMovieState`() {
        var eventPosted: MovieStateEvent? = null
        val session = mockk<Session>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns null

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.updateWatchlistMovieState(12.0, true)
        assertEquals(MovieStateEvent.UserNotLogged, eventPosted)
        verify(exactly = 0) { movieStateRepository.updateWatchlistMovieState(any(), any(), any(), any()) }
    }

    @Test
    fun `Should update favorite state and post result when session and account data available updateWatchlistMovieState`() {
        var eventPosted: MovieStateEvent? = null
        val session = mockk<Session>()
        val userAccount = mockk<UserAccount>()
        val expected = MovieStateEvent.UpdateWatchlist(true)

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns userAccount
        every { movieStateRepository.updateWatchlistMovieState(any(), any(), any(), any()) } returns true

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.updateWatchlistMovieState(12.0, true)
        assertEquals(expected, eventPosted)
        verify { movieStateRepository.updateWatchlistMovieState(12.0, true, userAccount, session) }
        verify { moviePageRepository.flushWatchlistMoviePages() }
    }

    @Test
    fun `Should post user not logged when there is no session created on rateMovie`() {
        var eventPosted: MovieStateEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns null

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.rateMovie(12.0, 5.5F)
        assertEquals(MovieStateEvent.UserNotLogged, eventPosted)
        verify(exactly = 0) { movieStateRepository.rateMovie(any(), any(), any(), any()) }
    }

    @Test
    fun `Should post user not logged when there is a session created but no user account data on rateMovie`() {
        var eventPosted: MovieStateEvent? = null
        val session = mockk<Session>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns null

        subject.movieStateEvents.observeWith { eventPosted = it }

        subject.rateMovie(12.0, 5.5F)
        assertEquals(MovieStateEvent.UserNotLogged, eventPosted)
        verify(exactly = 0) { movieStateRepository.rateMovie(any(), any(), any(), any()) }
    }

    @Test
    fun `Should rate movie and post result when session and account data available rateMovie`() {
        var eventPosted: MovieDetailsInteractor.RateMovieEvent? = null
        val session = mockk<Session>()
        val userAccount = mockk<UserAccount>()
        val expected = MovieDetailsInteractor.RateMovieEvent.RateMovie(true)

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns userAccount
        every { movieStateRepository.rateMovie(any(), any(), any(), any()) } returns true

        subject.rateMovieEvents.observeWith { eventPosted = it }

        subject.rateMovie(12.0, 5.5f)
        assertEquals(expected, eventPosted)
        verify { movieStateRepository.rateMovie(12.0, 5.5F, userAccount, session) }
        verify { moviePageRepository.flushRatedMoviePages() }
    }

    @Test
    fun `Should rate move and post error result when session and account data available rateMovie`() {
        var eventPosted: MovieDetailsInteractor.RateMovieEvent? = null
        val session = mockk<Session>()
        val userAccount = mockk<UserAccount>()
        val expected = MovieDetailsInteractor.RateMovieEvent.RateMovie(false)

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns userAccount
        every { movieStateRepository.rateMovie(any(), any(), any(), any()) } returns false

        subject.rateMovieEvents.observeWith { eventPosted = it }

        subject.rateMovie(12.0, 5.5f)
        assertEquals(expected, eventPosted)
        verify { movieStateRepository.rateMovie(12.0, 5.5F, userAccount, session) }
        verify { moviePageRepository.flushRatedMoviePages() }
    }

    @Test
    fun `Should post NotConnectedToNetwork when trying to rate movie in disconnected state`() {
        var eventPosted: MovieDetailsInteractor.RateMovieEvent? = null
        val session = mockk<Session>()
        val userAccount = mockk<UserAccount>()
        val expected = MovieDetailsInteractor.RateMovieEvent.NotConnectedToNetwork

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected
        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns userAccount
        every { movieStateRepository.rateMovie(any(), any(), any(), any()) } returns true

        subject.rateMovieEvents.observeWith { eventPosted = it }

        subject.rateMovie(12.0, 5.5f)
        assertEquals(expected, eventPosted)
        verify(exactly = 0) { movieStateRepository.rateMovie(12.0, 5.5F, userAccount, session) }
        verify(exactly = 0) { moviePageRepository.flushRatedMoviePages() }
    }
}
