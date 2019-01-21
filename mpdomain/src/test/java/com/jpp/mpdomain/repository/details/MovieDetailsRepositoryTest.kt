package com.jpp.mpdomain.repository.details

import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.handlers.ConnectivityHandler
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

@ExtendWith(MockKExtension::class)
class MovieDetailsRepositoryTest {

    @MockK
    private lateinit var detailsApi: MovieDetailsApi
    @RelaxedMockK
    private lateinit var detailsDb: MovieDetailsDb
    @MockK
    private lateinit var connectivityHandler: ConnectivityHandler

    private lateinit var subject: MovieDetailsRepository

    @BeforeEach
    fun setUp() {
        subject = MovieDetailsRepositoryImpl(detailsApi, detailsDb, connectivityHandler)
    }

    @Test
    fun `getDetail when is not connected to internet should return ErrorNoConnectivity`() {
        every { connectivityHandler.isConnectedToNetwork() } returns false

        val result = subject.getDetail(10.toDouble())

        assertEquals(MovieDetailsRepositoryState.ErrorNoConnectivity, result)
        verify(exactly = 0) { detailsDb.getMovieDetails(10.toDouble()) }
        verify(exactly = 0) { detailsApi.getMovieDetails(any()) }
    }

    @Test
    fun `getDetail when details is in DB should not get data from API`() {
        val movieDetail = mockk<MovieDetail>()
        every { connectivityHandler.isConnectedToNetwork() } returns true
        every { detailsDb.getMovieDetails(any()) } returns movieDetail

        val result = subject.getDetail(10.toDouble())

        assertEquals(MovieDetailsRepositoryState.Success(movieDetail), result)
        verify { detailsDb.getMovieDetails(10.toDouble()) }
        verify(exactly = 0) { detailsApi.getMovieDetails(any()) }
    }

    @Test
    fun `getDetail when details is not in DB not get data from API and update the DB`() {
        val movieDetail = mockk<MovieDetail>()
        every { connectivityHandler.isConnectedToNetwork() } returns true
        every { detailsDb.getMovieDetails(any()) } returns null
        every { detailsApi.getMovieDetails(any()) } returns movieDetail

        val result = subject.getDetail(10.toDouble())

        assertEquals(MovieDetailsRepositoryState.Success(movieDetail), result)
        verify { detailsDb.getMovieDetails(10.toDouble()) }
        verify { detailsApi.getMovieDetails(10.toDouble()) }
        verify { detailsDb.saveMovieDetails(movieDetail) }
    }

    @Test
    fun `getDetail when details there is an error should return ErrorUnknown`() {
        every { connectivityHandler.isConnectedToNetwork() } returns true
        every { detailsDb.getMovieDetails(any()) } returns null
        every { detailsApi.getMovieDetails(any()) } returns null

        val result = subject.getDetail(10.toDouble())

        assertEquals(MovieDetailsRepositoryState.ErrorUnknown, result)
    }

}