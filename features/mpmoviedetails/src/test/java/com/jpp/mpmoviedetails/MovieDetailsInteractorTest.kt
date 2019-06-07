package com.jpp.mpmoviedetails

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MovieDetailRepository
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieDetailEvent
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieDetailEvent.*
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
class MovieDetailsInteractorTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @MockK
    private lateinit var movieDetailRepository: MovieDetailRepository
    @MockK
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: MovieDetailsInteractor

    @BeforeEach
    fun setUp() {
        subject = MovieDetailsInteractor(connectivityRepository,
                movieDetailRepository, languageRepository)
    }

    @Test
    fun `Should post not connected event when not connected to network`() {
        var eventPosted: MovieDetailEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.movieDetailEvents.observeWith { eventPosted = it }

        subject.fetchMovieDetail(12.0)

        assertEquals(NotConnectedToNetwork, eventPosted)
        verify(exactly = 0) { movieDetailRepository.getMovieDetails(any(), any()) }
    }

    @Test
    fun `Should post error unknown event when connected to network but fails to fetch movie details`() {
        var eventPosted: MovieDetailEvent? = null

        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { movieDetailRepository.getMovieDetails(any(), any()) } returns null

        subject.movieDetailEvents.observeWith { eventPosted = it }

        subject.fetchMovieDetail(12.0)

        assertEquals(UnknownError, eventPosted)
        verify { movieDetailRepository.getMovieDetails(12.0, SupportedLanguage.English) }
    }

    @Test
    fun `Should success when fetches movie details`() {
        var eventPosted: MovieDetailEvent? = null
        val movieDetail = mockk<MovieDetail>()
        val expected = Success(movieDetail)

        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { movieDetailRepository.getMovieDetails(any(), any()) } returns movieDetail

        subject.movieDetailEvents.observeWith { eventPosted = it }

        subject.fetchMovieDetail(12.0)

        assertEquals(expected, eventPosted)
        verify { movieDetailRepository.getMovieDetails(12.0, SupportedLanguage.English) }
    }
}