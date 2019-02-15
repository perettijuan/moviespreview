package com.jpp.mpdata.repository

import com.jpp.mpdata.repository.movies.MoviesApi
import com.jpp.mpdata.repository.movies.MoviesDb
import com.jpp.mpdata.repository.movies.MoviesRepositoryImpl
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.MoviesRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class MoviesRepositoryImplTest {

    @RelaxedMockK
    private lateinit var moviesApi: MoviesApi
    @RelaxedMockK
    private lateinit var moviesDb: MoviesDb

    private lateinit var subject: MoviesRepository

    @BeforeEach
    fun setUp() {
        subject = MoviesRepositoryImpl(moviesApi, moviesDb)
    }


    @ParameterizedTest
    @MethodSource("allMovieSections")
    fun `Should not retrieve from API when valid value in DB`(section: MovieSection) {
        val expected = mockk<MoviePage>()

        every { moviesDb.getMoviePageForSection(any(), any()) } returns expected

        val actual = subject.getMoviePageForSection(1, section)

        verify { moviesDb.getMoviePageForSection(1, section) }
        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @MethodSource("movieSectionsAndCount")
    fun `Should retrieve from API and update DB when value not in DB`(section: MovieSection, countInput: MoviesRepositoryTestInput) {
        val expected = mockk<MoviePage>()

        every { moviesDb.getMoviePageForSection(any(), any()) } returns null
        every { moviesApi.getNowPlayingMoviePage(any()) } returns expected
        every { moviesApi.getPopularMoviePage(any()) } returns expected
        every { moviesApi.getTopRatedMoviePage(any()) } returns expected
        every { moviesApi.getUpcomingMoviePage(any()) } returns expected

        val actual = subject.getMoviePageForSection(1, section)

        verify(exactly = countInput.callsToNowPlaying) { moviesApi.getNowPlayingMoviePage(1) }
        verify(exactly = countInput.callsToPopular) { moviesApi.getPopularMoviePage(1) }
        verify(exactly = countInput.callsToTopRated) { moviesApi.getTopRatedMoviePage(1) }
        verify(exactly = countInput.callsToUpcoming) { moviesApi.getUpcomingMoviePage(1) }

        verify { moviesDb.saveMoviePageForSection(expected, section) }

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @MethodSource("movieSectionsAndCount")
    fun `Should not attempt to store null responses from API`(section: MovieSection, countInput: MoviesRepositoryTestInput) {
        every { moviesDb.getMoviePageForSection(any(), any()) } returns null
        every { moviesApi.getNowPlayingMoviePage(any()) } returns null
        every { moviesApi.getPopularMoviePage(any()) } returns null
        every { moviesApi.getTopRatedMoviePage(any()) } returns null
        every { moviesApi.getUpcomingMoviePage(any()) } returns null

        val actual = subject.getMoviePageForSection(1, section)

        verify(exactly = countInput.callsToNowPlaying) { moviesApi.getNowPlayingMoviePage(1) }
        verify(exactly = countInput.callsToPopular) { moviesApi.getPopularMoviePage(1) }
        verify(exactly = countInput.callsToTopRated) { moviesApi.getTopRatedMoviePage(1) }
        verify(exactly = countInput.callsToUpcoming) { moviesApi.getUpcomingMoviePage(1) }

        verify(exactly = 0) { moviesDb.saveMoviePageForSection(any(), any()) }

        assertNull(actual)
    }

    data class MoviesRepositoryTestInput(
            val callsToNowPlaying: Int = 0,
            val callsToTopRated: Int = 0,
            val callsToPopular: Int = 0,
            val callsToUpcoming: Int = 0
    )

    companion object {

        @JvmStatic
        fun allMovieSections() = listOf(
                MovieSection.Playing,
                MovieSection.Upcoming,
                MovieSection.Popular,
                MovieSection.TopRated
        )


        @JvmStatic
        fun movieSectionsAndCount() = listOf(
                arguments(MovieSection.Playing, MoviesRepositoryTestInput(callsToNowPlaying = 1)),
                arguments(MovieSection.Popular, MoviesRepositoryTestInput(callsToPopular = 1)),
                arguments(MovieSection.Upcoming, MoviesRepositoryTestInput(callsToUpcoming = 1)),
                arguments(MovieSection.TopRated, MoviesRepositoryTestInput(callsToTopRated = 1))
        )
    }

}