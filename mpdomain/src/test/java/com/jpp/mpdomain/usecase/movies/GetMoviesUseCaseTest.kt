package com.jpp.mpdomain.usecase.movies

import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
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
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class GetMoviesUseCaseTest {

    @RelaxedMockK
    private lateinit var moviesRepository: MoviesRepository
    @RelaxedMockK
    private lateinit var connectivityHandler: ConnectivityHandler

    private lateinit var subject: GetMoviesUseCase

    @BeforeEach
    fun setUp() {
        subject = GetMoviesUseCase.Impl(moviesRepository, connectivityHandler)
    }

    @ParameterizedTest
    @MethodSource("allMovieSections")
    fun `Should check connectivity before searching and return ErrorNoConnectivity`(movieSection: MovieSection) {
        every { connectivityHandler.isConnectedToNetwork() } returns false

        subject.getMoviePageForSection(1, movieSection).let { result ->
            verify(exactly = 0) { moviesRepository.getMoviePageForSection(any(), any()) }
            assertEquals(GetMoviesUseCaseResult.ErrorNoConnectivity, result)
        }
    }

    @ParameterizedTest
    @MethodSource("allMovieSections")
    fun `Should return ErrorUnknown when connected to network and an error occurs`(movieSection: MovieSection) {
        every { connectivityHandler.isConnectedToNetwork() } returns true
        every { moviesRepository.getMoviePageForSection(any(), any()) } returns null

        subject.getMoviePageForSection(1, movieSection).let { result ->
            verify(exactly = 1) { moviesRepository.getMoviePageForSection(any(), any()) }
            assertEquals(GetMoviesUseCaseResult.ErrorUnknown, result)
        }
    }

    @ParameterizedTest
    @MethodSource("allMovieSections")
    fun `Should return Success when connected to network and an can fetch movie page`(movieSection: MovieSection) {
        val moviePage = mockk<MoviePage>()
        every { connectivityHandler.isConnectedToNetwork() } returns true
        every { moviesRepository.getMoviePageForSection(any(), any()) } returns moviePage

        subject.getMoviePageForSection(1, movieSection).let { result ->
            verify(exactly = 1) { moviesRepository.getMoviePageForSection(any(), any()) }
            assertTrue(result is GetMoviesUseCaseResult.Success)
            assertEquals((result as GetMoviesUseCaseResult.Success).moviesPage, moviePage)
        }
    }

    companion object {
        @JvmStatic
        fun allMovieSections() = listOf(
                MovieSection.Playing,
                MovieSection.Upcoming,
                MovieSection.Popular,
                MovieSection.TopRated
        )
    }
}