package com.jpp.mpdata.repository.movies

import com.jpp.mpdata.datasources.moviepage.MoviesApi
import com.jpp.mpdata.datasources.moviepage.MoviesDb
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.MoviePageRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class MoviePageRepositoryImplTest {

    @RelaxedMockK
    private lateinit var moviesApi: MoviesApi
    @RelaxedMockK
    private lateinit var moviesDb: MoviesDb

    private lateinit var subject: MoviePageRepository

    @BeforeEach
    fun setUp() {
        subject = MoviePageRepositoryImpl(moviesApi, moviesDb)
    }

    @ParameterizedTest
    @MethodSource("allMovieSections")
    fun `Should not retrieve from API when valid movie page in DB`(section: MovieSection) = runBlocking {
        val expected = mockk<MoviePage>()

        every { moviesDb.getMoviePageForSection(any(), any()) } returns expected

        val actual = subject.getMoviePageForSection(1, section, SupportedLanguage.English)

        verify { moviesDb.getMoviePageForSection(1, section) }
        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @MethodSource("movieSectionsAndCount")
    fun `Should retrieve from API and update DB when movie page not in DB`(section: MovieSection, countInput: MoviesRepositoryTestInput) = runBlocking {
        val expected = mockk<MoviePage>()

        every { moviesDb.getMoviePageForSection(any(), any()) } returns null
        every { moviesApi.getNowPlayingMoviePage(any(), any()) } returns expected
        every { moviesApi.getPopularMoviePage(any(), any()) } returns expected
        every { moviesApi.getTopRatedMoviePage(any(), any()) } returns expected
        every { moviesApi.getUpcomingMoviePage(any(), any()) } returns expected

        val actual = subject.getMoviePageForSection(1, section, SupportedLanguage.English)

        verify(exactly = countInput.callsToNowPlaying) { moviesApi.getNowPlayingMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToPopular) { moviesApi.getPopularMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToTopRated) { moviesApi.getTopRatedMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToUpcoming) { moviesApi.getUpcomingMoviePage(1, SupportedLanguage.English) }

        verify { moviesDb.saveMoviePageForSection(expected, section) }

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @MethodSource("movieSectionsAndCount")
    fun `Should not attempt to store null responses from API when fetching movie page`(section: MovieSection, countInput: MoviesRepositoryTestInput) = runBlocking {
        every { moviesDb.getMoviePageForSection(any(), any()) } returns null
        every { moviesApi.getNowPlayingMoviePage(any(), any()) } returns null
        every { moviesApi.getPopularMoviePage(any(), any()) } returns null
        every { moviesApi.getTopRatedMoviePage(any(), any()) } returns null
        every { moviesApi.getUpcomingMoviePage(any(), any()) } returns null

        val actual = subject.getMoviePageForSection(1, section, SupportedLanguage.English)

        verify(exactly = countInput.callsToNowPlaying) { moviesApi.getNowPlayingMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToPopular) { moviesApi.getPopularMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToTopRated) { moviesApi.getTopRatedMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToUpcoming) { moviesApi.getUpcomingMoviePage(1, SupportedLanguage.English) }

        verify(exactly = 0) { moviesDb.saveMoviePageForSection(any(), any()) }

        assertNull(actual)
    }

    @Test
    fun `Should not retrieve favorite movie from API when stored in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviesDb.getFavoriteMovies(1) } returns moviePage

        val retrieved = subject.getFavoriteMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 0) { moviesApi.getFavoriteMoviePage(any(), any(), any(), any()) }
    }

    @Test
    fun `Should retrieve favorite movie from API and store it in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviesDb.getFavoriteMovies(1) } returns null
        every { moviesApi.getFavoriteMoviePage(1, any(), any(), any()) } returns moviePage

        val retrieved = subject.getFavoriteMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 1) { moviesApi.getFavoriteMoviePage(any(), any(), any(), any()) }
        verify { moviesDb.saveFavoriteMoviesPage(1, moviePage) }
    }

    @Test
    fun `Should return null and not attempt to save favorite movie when API returns null`() = runBlocking {
        every { moviesDb.getFavoriteMovies(1) } returns null
        every { moviesApi.getFavoriteMoviePage(1, any(), any(), any()) } returns null

        val retrieved = subject.getFavoriteMoviePage(1, mockk(), mockk(), mockk())

        assertNull(retrieved)
        verify(exactly = 1) { moviesApi.getFavoriteMoviePage(any(), any(), any(), any()) }
        verify(exactly = 0) { moviesDb.saveFavoriteMoviesPage(1, any()) }
    }

    @Test
    fun `Should not retrieve rated movie from API when stored in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviesDb.getRatedMovies(1) } returns moviePage

        val retrieved = subject.getRatedMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 0) { moviesApi.getRatedMoviePage(any(), any(), any(), any()) }
    }

    @Test
    fun `Should retrieve rated movie from API and store it in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviesDb.getRatedMovies(1) } returns null
        every { moviesApi.getRatedMoviePage(1, any(), any(), any()) } returns moviePage

        val retrieved = subject.getRatedMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 1) { moviesApi.getRatedMoviePage(any(), any(), any(), any()) }
        verify { moviesDb.saveRatedMoviesPage(1, moviePage) }
    }

    @Test
    fun `Should return null and not attempt to save rated movie when API returns null`() = runBlocking {
        every { moviesDb.getRatedMovies(1) } returns null
        every { moviesApi.getRatedMoviePage(1, any(), any(), any()) } returns null

        val retrieved = subject.getRatedMoviePage(1, mockk(), mockk(), mockk())

        assertNull(retrieved)
        verify(exactly = 1) { moviesApi.getRatedMoviePage(any(), any(), any(), any()) }
        verify(exactly = 0) { moviesDb.saveRatedMoviesPage(1, any()) }
    }

    @Test
    fun `Should not retrieve watchlist page from API when stored in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviesDb.getWatchlistMoviePage(1) } returns moviePage

        val retrieved = subject.getWatchlistMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 0) { moviesApi.getWatchlistMoviePage(any(), any(), any(), any()) }
    }

    @Test
    fun `Should retrieve watchlist page from API and store it in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviesDb.getWatchlistMoviePage(1) } returns null
        every { moviesApi.getWatchlistMoviePage(1, any(), any(), any()) } returns moviePage

        val retrieved = subject.getWatchlistMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 1) { moviesApi.getWatchlistMoviePage(any(), any(), any(), any()) }
        verify { moviesDb.saveWatchlistMoviePage(1, moviePage) }
    }

    @Test
    fun `Should return null and not attempt to save watchlist page when API returns null`() = runBlocking {
        every { moviesDb.getWatchlistMoviePage(1) } returns null
        every { moviesApi.getWatchlistMoviePage(1, any(), any(), any()) } returns null

        val retrieved = subject.getWatchlistMoviePage(1, mockk(), mockk(), mockk())

        assertNull(retrieved)
        verify(exactly = 1) { moviesApi.getWatchlistMoviePage(any(), any(), any(), any()) }
        verify(exactly = 0) { moviesDb.saveWatchlistMoviePage(1, any()) }
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
