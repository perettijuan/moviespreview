package com.jpp.mpdata.repository.account

import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.account.AccountDb
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

//TODO JPP do something with me
@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class AccountRepositoryTest {

    @RelaxedMockK
    private lateinit var accountApi: AccountApi
    @RelaxedMockK
    private lateinit var accountDb: AccountDb

    private lateinit var subject: AccountRepository

    @BeforeEach
    fun setUp() {
        subject = AccountRepositoryImpl(accountApi, accountDb)
    }

//    @Test
//    fun `Should not fetch data from API if present in DB`() {
//        val expected = mockk<UserAccount>()
//        val session = mockk<Session>()
//
//        every { accountDb.getUserAccountInfo() } returns expected
//
//        val actual = subject.getUserAccount(session)
//
//        assertEquals(expected, actual)
//        verify(exactly = 0) { accountApi.getUserAccountInfo(any()) }
//    }
//
//
//    @Test
//    fun `Should update DB if data fetched from API`() {
//        val expected = mockk<UserAccount>()
//        val session = mockk<Session>()
//
//        every { accountDb.getUserAccountInfo() } returns null
//        every { accountApi.getUserAccountInfo(session) } returns expected
//
//        val actual = subject.getUserAccount(session)
//
//        assertEquals(expected, actual)
//        verify { accountApi.getUserAccountInfo(any()) }
//        verify { accountDb.storeUserAccountInfo(expected) }
//    }
//
//    @Test
//    fun `Should return false if fails to update favorite movie`() {
//        every { accountApi.updateMovieFavoriteState(any(), any(), any(), any()) } returns null
//
//        val result = subject.updateMovieFavoriteState(12.toDouble(), false, mockk(), mockk())
//
//        assertFalse(result)
//    }
//
//
//    @Test
//    fun `Should flush data in DB if successfully update favorites`() {
//        every { accountApi.updateMovieFavoriteState(any(), any(), any(), any()) } returns true
//
//        val result = subject.updateMovieFavoriteState(12.toDouble(), false, mockk(), mockk())
//
//        verify { accountDb.flushData() }
//        assertTrue(result)
//    }
//
//    @Test
//    fun `Should notify data update when successfully update favorites`() {
//        var stateUpdatePosted: AccountRepository.AccountDataUpdate? = null
//
//        subject.updates().observeWith { stateUpdatePosted = it }
//
//        subject.updateMovieFavoriteState(12.toDouble(), false, mockk(), mockk())
//        assertEquals(AccountRepository.AccountDataUpdate.FavoritesMovies, stateUpdatePosted)
//    }
//
//
//    @Test
//    fun `Should not fetch favorites movie page if data in DB`() {
//        val userAccount = mockk<UserAccount>()
//        val session = mockk<Session>()
//        val language = SupportedLanguage.English
//
//        val moviePage = mockk<MoviePage>()
//
//        every { accountDb.getFavoriteMovies(any()) } returns moviePage
//
//        val actualPage = subject.getFavoriteMovies(1, userAccount, session, language)
//
//        verify(exactly = 0) { accountApi.getFavoriteMovies(any(), any(), any(), any()) }
//        assertEquals(moviePage, actualPage)
//    }
//
//    @Test
//    fun `Should update favorite movie page DB if data fetched from API`() {
//        val userAccount = mockk<UserAccount>()
//        val session = mockk<Session>()
//        val language = SupportedLanguage.English
//
//        val moviePage = mockk<MoviePage>()
//
//        every { accountDb.getFavoriteMovies(any()) } returns null
//        every { accountApi.getFavoriteMovies(any(), any(), any(), any()) } returns moviePage
//
//        val actualPage = subject.getFavoriteMovies(1, userAccount, session, language)
//
//        assertEquals(moviePage, actualPage)
//        verify { accountDb.saveFavoriteMoviesPage(1, moviePage) }
//    }

}