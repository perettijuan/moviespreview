package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MovieGenreRepository
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetAllMovieGenresUseCaseTest {

    @MockK
    private lateinit var movieGenreRepository: MovieGenreRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: GetAllMovieGenresUseCase

    @BeforeEach
    fun setUp() {
        subject = GetAllMovieGenresUseCase(movieGenreRepository, connectivityRepository)
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        coEvery { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected
        coEvery { movieGenreRepository.getMovieGenres() } returns null

        val actual = subject.execute()

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason`() = runBlocking {
        coEvery { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { movieGenreRepository.getMovieGenres() } returns null

        val actual = subject.execute()

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should return genre list`() = runBlocking {
        val expectedList = listOf<MovieGenre>()
        coEvery { movieGenreRepository.getMovieGenres() } returns expectedList

        val actual = subject.execute()

        assertTrue(actual is Try.Success)
        assertEquals(expectedList, (actual as Try.Success).value)
    }
}
