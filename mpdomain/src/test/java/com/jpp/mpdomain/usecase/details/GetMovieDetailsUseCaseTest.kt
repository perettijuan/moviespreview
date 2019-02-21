package com.jpp.mpdomain.usecase.details

import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.handlers.ConnectivityHandler
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
    private lateinit var connectivityHandler: ConnectivityHandler

    private lateinit var subject: GetMovieDetailsUseCase

    @BeforeEach
    fun setUp() {
        subject = GetMovieDetailsUseCase.Impl(moviesRepository, connectivityHandler)
    }

    @Test
    fun `Should check connectivity before searching and return ErrorNoConnectivity`() {
        every { connectivityHandler.isConnectedToNetwork() } returns false

        subject.getDetailsForMovie(1.toDouble()).let { result ->
            verify(exactly = 0) { moviesRepository.getMovieDetails(any()) }
            assertEquals(GetMovieDetailsUseCaseResult.ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        every { connectivityHandler.isConnectedToNetwork() } returns true
        every { moviesRepository.getMovieDetails(any()) } returns null

        subject.getDetailsForMovie(1.toDouble()).let { result ->
            verify(exactly = 1) { moviesRepository.getMovieDetails(any()) }
            assertEquals(GetMovieDetailsUseCaseResult.ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected to network and an can get details`() {
        val details = mockk<MovieDetail>()
        every { connectivityHandler.isConnectedToNetwork() } returns true
        every { moviesRepository.getMovieDetails(any()) } returns details

        subject.getDetailsForMovie(1.toDouble()).let { result ->
            verify(exactly = 1) { moviesRepository.getMovieDetails(any()) }
            assertTrue(result is GetMovieDetailsUseCaseResult.Success)
            assertEquals((result as GetMovieDetailsUseCaseResult.Success).details, details)
        }
    }
}