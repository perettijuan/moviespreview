package com.jpp.mpaccount.account.lists

import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.NotConnectedToNetwork
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.UnknownError
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.UserChangedLanguage
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.UserNotLogged
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.SessionRepository
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
class UserMovieListInteractorTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @RelaxedMockK
    private lateinit var accountRepository: AccountRepository
    @RelaxedMockK
    private lateinit var sessionRepository: SessionRepository
    @RelaxedMockK
    private lateinit var moviePageRepository: MoviePageRepository
    @MockK
    private lateinit var languageRepository: LanguageRepository

    private val languageRepositoryLiveData = MutableLiveData<SupportedLanguage>()

    private lateinit var subject: UserMovieListInteractor

    @BeforeEach
    fun setUp() {
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        every { languageRepository.updates() } returns languageRepositoryLiveData

        subject = UserMovieListInteractor(
                connectivityRepository,
                sessionRepository,
                accountRepository,
                moviePageRepository,
                languageRepository
        )
    }

    @Test
    fun `Should post user not logged when no session available`() {
        var eventPosted: UserMovieListInteractor.UserMovieListEvent? = null

        every { sessionRepository.getCurrentSession() } returns null

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchFavoriteMovies(1, mockk())

        assertEquals(UserNotLogged, eventPosted)
        verify(exactly = 0) { accountRepository.getUserAccount(any()) }
        verify(exactly = 0) { moviePageRepository.getFavoriteMoviePage(any(), any(), any(), any()) }
        verify(exactly = 0) { languageRepository.getCurrentAppLanguage() }
    }

    @Test
    fun `Should post not connected event when user logged in but no connected to network`() {
        var eventPosted: UserMovieListInteractor.UserMovieListEvent? = null

        every { sessionRepository.getCurrentSession() } returns mockk()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchFavoriteMovies(1, mockk())

        assertEquals(NotConnectedToNetwork, eventPosted)
        verify(exactly = 1) { accountRepository.getUserAccount(any()) }
        verify(exactly = 0) { moviePageRepository.getFavoriteMoviePage(any(), any(), any(), any()) }
        verify(exactly = 0) { languageRepository.getCurrentAppLanguage() }
    }

    @Test
    fun `Should post unknown error event when user logged and connected to network but fails to fetch data`() {
        var eventPosted: UserMovieListInteractor.UserMovieListEvent? = null

        every { sessionRepository.getCurrentSession() } returns mockk()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { accountRepository.getUserAccount(any()) } returns mockk()
        every { moviePageRepository.getFavoriteMoviePage(any(), any(), any(), any()) } returns null

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchFavoriteMovies(1, mockk())

        assertEquals(UnknownError, eventPosted)
        verify(exactly = 1) { accountRepository.getUserAccount(any()) }
        verify(exactly = 1) { moviePageRepository.getFavoriteMoviePage(any(), any(), any(), any()) }
        verify(exactly = 1) { languageRepository.getCurrentAppLanguage() }
    }

    @Test
    fun `Should execute callback with favorite movie list`() {
        val movieList = listOf<Movie>(mockk(), mockk(), mockk())
        val moviePage = mockk<MoviePage>()
        val session = mockk<Session>()
        val accountData = mockk<UserAccount>()
        var result: List<Movie>? = null
        val callback: (List<Movie>) -> Unit = { result = it }

        every { moviePage.results } returns movieList
        every { sessionRepository.getCurrentSession() } returns session
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { accountRepository.getUserAccount(any()) } returns accountData
        every { moviePageRepository.getFavoriteMoviePage(any(), any(), any(), any()) } returns moviePage

        subject.fetchFavoriteMovies(1, callback)

        assertEquals(movieList, result)
        verify(exactly = 1) { accountRepository.getUserAccount(session) }
        verify(exactly = 1) { moviePageRepository.getFavoriteMoviePage(1, accountData, session, SupportedLanguage.English) }
        verify(exactly = 1) { languageRepository.getCurrentAppLanguage() }
    }

    @Test
    fun `Should notify when user changes language`() {
        var eventPosted: UserMovieListInteractor.UserMovieListEvent? = null

        subject.userAccountEvents.observeWith { eventPosted = it }

        languageRepositoryLiveData.postValue(SupportedLanguage.Spanish)

        assertEquals(UserChangedLanguage, eventPosted)
    }
}
