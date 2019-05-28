package com.jpp.mpaccount.account

import com.jpp.mpaccount.account.UserAccountInteractor.UserAccountEvent
import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.*
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class UserAccountInteractorTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @MockK
    private lateinit var accountRepository: AccountRepository
    @MockK
    private lateinit var sessionRepository: SessionRepository
    @MockK
    private lateinit var moviesRepository: MoviesRepository
    @MockK
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: UserAccountInteractor

    @BeforeEach
    fun setUp() {
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        subject = UserAccountInteractor(
                connectivityRepository,
                sessionRepository,
                accountRepository,
                moviesRepository,
                languageRepository
        )
    }

    @Test
    fun `Should post user not logged when no session available`() {
        var eventPosted: UserAccountEvent? = null

        every { sessionRepository.getCurrentSession() } returns null

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchUserAccountData()

        assertEquals(UserAccountEvent.UserNotLogged, eventPosted)
        verify(exactly = 0) { accountRepository.getUserAccount(any()) }
        verify(exactly = 0) { moviesRepository.getFavoriteMovies(any(), any(), any(), any()) }
        verify(exactly = 0) { languageRepository.getCurrentAppLanguage() }
    }

    @Test
    fun `Should post not connected event when user logged in but no connected to network`() {
        var eventPosted: UserAccountEvent? = null

        every { sessionRepository.getCurrentSession() } returns mockk()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchUserAccountData()

        assertEquals(UserAccountEvent.NotConnectedToNetwork, eventPosted)
        verify(exactly = 0) { accountRepository.getUserAccount(any()) }
        verify(exactly = 0) { moviesRepository.getFavoriteMovies(any(), any(), any(), any()) }
        verify(exactly = 1) { languageRepository.getCurrentAppLanguage() }
    }

    @Test
    fun `Should post unknown error event when user logged and connected to network but fails to fetch data`() {
        var eventPosted: UserAccountEvent? = null

        every { sessionRepository.getCurrentSession() } returns mockk()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { accountRepository.getUserAccount(any()) } returns null

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchUserAccountData()

        assertEquals(UserAccountEvent.UnknownError, eventPosted)
        verify(exactly = 1) { accountRepository.getUserAccount(any()) }
        verify(exactly = 0) { moviesRepository.getFavoriteMovies(any(), any(), any(), any()) }
        verify(exactly = 1) { languageRepository.getCurrentAppLanguage() }
    }

    @Test
    fun `Should post success event when user logged and connected to network and can fetch user account data`() {
        var eventPosted: UserAccountEvent? = null
        val session = mockk<Session>()
        val accountData = mockk<UserAccount>()
        val favMoviePage = mockk<MoviePage>()
        val ratedMoviePage = mockk<MoviePage>()
        val expected = UserAccountEvent.Success(
                accountData,
                UserAccountInteractor.UserMoviesState.Success(favMoviePage),
                UserAccountInteractor.UserMoviesState.Success(ratedMoviePage)
        )

        every { sessionRepository.getCurrentSession() } returns session
        every { accountRepository.getUserAccount(any()) } returns accountData
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { accountRepository.getUserAccount(any()) } returns accountData
        every { moviesRepository.getFavoriteMovies(any(), any(), any(), any()) } returns favMoviePage
        every { moviesRepository.getRatedMovies(any(), any(), any(), any()) } returns ratedMoviePage

        subject.userAccountEvents.observeWith { eventPosted = it }

        subject.fetchUserAccountData()

        assertEquals(expected, eventPosted)
        verify(exactly = 1) { accountRepository.getUserAccount(session) }
        verify(exactly = 1) { moviesRepository.getFavoriteMovies(1, accountData, session, SupportedLanguage.English) }
        verify(exactly = 1) { moviesRepository.getRatedMovies(1, accountData, session, SupportedLanguage.English) }
        verify(exactly = 1) { languageRepository.getCurrentAppLanguage() }
    }
}