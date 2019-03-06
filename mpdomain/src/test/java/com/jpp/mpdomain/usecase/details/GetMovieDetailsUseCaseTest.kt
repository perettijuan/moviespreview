package com.jpp.mpdomain.usecase.details

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MoviesRepository
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

@ExtendWith(MockKExtension::class)
class GetMovieDetailsUseCaseTest {

    @RelaxedMockK
    private lateinit var moviesRepository: MoviesRepository
    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: GetMovieDetailsUseCase

    @BeforeEach
    fun setUp() {
        subject = GetMovieDetailsUseCase.Impl(moviesRepository, connectivityRepository)
    }

    @Test
    fun `Should check connectivity before fetching details and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.getDetailsForMovie(1.toDouble()).let { result ->
            verify(exactly = 0) { moviesRepository.getMovieDetails(any()) }
            assertEquals(GetMovieDetailsResult.ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { moviesRepository.getMovieDetails(any()) } returns null

        subject.getDetailsForMovie(1.toDouble()).let { result ->
            verify(exactly = 1) { moviesRepository.getMovieDetails(any()) }
            assertEquals(GetMovieDetailsResult.ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected to network and an can get details`() {
        val details = mockk<MovieDetail>()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { moviesRepository.getMovieDetails(any()) } returns details

        subject.getDetailsForMovie(1.toDouble()).let { result ->
            verify(exactly = 1) { moviesRepository.getMovieDetails(any()) }
            assertTrue(result is GetMovieDetailsResult.Success)
            assertEquals((result as GetMovieDetailsResult.Success).details, details)
        }
    }
}