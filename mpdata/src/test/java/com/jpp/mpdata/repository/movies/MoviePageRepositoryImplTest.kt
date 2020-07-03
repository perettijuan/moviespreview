package com.jpp.mpdata.repository.movies

import com.jpp.mpdata.datasources.moviepage.MoviePageApi
import com.jpp.mpdata.datasources.moviepage.MoviePageDb
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
    private lateinit var moviePageApi: MoviePageApi
    @RelaxedMockK
    private lateinit var moviePageDb: MoviePageDb

    private lateinit var subject: MoviePageRepository

    @BeforeEach
    fun setUp() {
        subject = MoviePageRepositoryImpl(moviePageApi, moviePageDb)
    }

    @ParameterizedTest
    @MethodSource("allMovieSections")
    fun `Should not retrieve from API when valid movie page in DB`(section: MovieSection) = runBlocking {
        val expected = mockk<MoviePage>()

        every { moviePageDb.getMoviePageForSection(any(), any()) } returns expected

        val actual = subject.getMoviePageForSection(1, section, SupportedLanguage.English)

        verify { moviePageDb.getMoviePageForSection(1, section) }
        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @MethodSource("movieSectionsAndCount")
    fun `Should retrieve from API and update DB when movie page not in DB`(section: MovieSection, countInput: MoviesRepositoryTestInput) = runBlocking {
        val expected = mockk<MoviePage>()

        every { moviePageDb.getMoviePageForSection(any(), any()) } returns null
        every { moviePageApi.getNowPlayingMoviePage(any(), any()) } returns expected
        every { moviePageApi.getPopularMoviePage(any(), any()) } returns expected
        every { moviePageApi.getTopRatedMoviePage(any(), any()) } returns expected
        every { moviePageApi.getUpcomingMoviePage(any(), any()) } returns expected

        val actual = subject.getMoviePageForSection(1, section, SupportedLanguage.English)

        verify(exactly = countInput.callsToNowPlaying) { moviePageApi.getNowPlayingMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToPopular) { moviePageApi.getPopularMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToTopRated) { moviePageApi.getTopRatedMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToUpcoming) { moviePageApi.getUpcomingMoviePage(1, SupportedLanguage.English) }

        verify { moviePageDb.saveMoviePageForSection(expected, section) }

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @MethodSource("movieSectionsAndCount")
    fun `Should not attempt to store null responses from API when fetching movie page`(section: MovieSection, countInput: MoviesRepositoryTestInput) = runBlocking {
        every { moviePageDb.getMoviePageForSection(any(), any()) } returns null
        every { moviePageApi.getNowPlayingMoviePage(any(), any()) } returns null
        every { moviePageApi.getPopularMoviePage(any(), any()) } returns null
        every { moviePageApi.getTopRatedMoviePage(any(), any()) } returns null
        every { moviePageApi.getUpcomingMoviePage(any(), any()) } returns null

        val actual = subject.getMoviePageForSection(1, section, SupportedLanguage.English)

        verify(exactly = countInput.callsToNowPlaying) { moviePageApi.getNowPlayingMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToPopular) { moviePageApi.getPopularMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToTopRated) { moviePageApi.getTopRatedMoviePage(1, SupportedLanguage.English) }
        verify(exactly = countInput.callsToUpcoming) { moviePageApi.getUpcomingMoviePage(1, SupportedLanguage.English) }

        verify(exactly = 0) { moviePageDb.saveMoviePageForSection(any(), any()) }

        assertNull(actual)
    }

    @Test
    fun `Should not retrieve favorite movie from API when stored in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviePageDb.getFavoriteMovies(1) } returns moviePage

        val retrieved = subject.getFavoriteMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 0) { moviePageApi.getFavoriteMoviePage(any(), any(), any(), any()) }
    }

    @Test
    fun `Should retrieve favorite movie from API and store it in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviePageDb.getFavoriteMovies(1) } returns null
        every { moviePageApi.getFavoriteMoviePage(1, any(), any(), any()) } returns moviePage

        val retrieved = subject.getFavoriteMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 1) { moviePageApi.getFavoriteMoviePage(any(), any(), any(), any()) }
        verify { moviePageDb.saveFavoriteMoviesPage(1, moviePage) }
    }

    @Test
    fun `Should return null and not attempt to save favorite movie when API returns null`() = runBlocking {
        every { moviePageDb.getFavoriteMovies(1) } returns null
        every { moviePageApi.getFavoriteMoviePage(1, any(), any(), any()) } returns null

        val retrieved = subject.getFavoriteMoviePage(1, mockk(), mockk(), mockk())

        assertNull(retrieved)
        verify(exactly = 1) { moviePageApi.getFavoriteMoviePage(any(), any(), any(), any()) }
        verify(exactly = 0) { moviePageDb.saveFavoriteMoviesPage(1, any()) }
    }

    @Test
    fun `Should not retrieve rated movie from API when stored in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviePageDb.getRatedMovies(1) } returns moviePage

        val retrieved = subject.getRatedMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 0) { moviePageApi.getRatedMoviePage(any(), any(), any(), any()) }
    }

    @Test
    fun `Should retrieve rated movie from API and store it in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviePageDb.getRatedMovies(1) } returns null
        every { moviePageApi.getRatedMoviePage(1, any(), any(), any()) } returns moviePage

        val retrieved = subject.getRatedMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 1) { moviePageApi.getRatedMoviePage(any(), any(), any(), any()) }
        verify { moviePageDb.saveRatedMoviesPage(1, moviePage) }
    }

    @Test
    fun `Should return null and not attempt to save rated movie when API returns null`() = runBlocking {
        every { moviePageDb.getRatedMovies(1) } returns null
        every { moviePageApi.getRatedMoviePage(1, any(), any(), any()) } returns null

        val retrieved = subject.getRatedMoviePage(1, mockk(), mockk(), mockk())

        assertNull(retrieved)
        verify(exactly = 1) { moviePageApi.getRatedMoviePage(any(), any(), any(), any()) }
        verify(exactly = 0) { moviePageDb.saveRatedMoviesPage(1, any()) }
    }

    @Test
    fun `Should not retrieve watchlist page from API when stored in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviePageDb.getWatchlistMoviePage(1) } returns moviePage

        val retrieved = subject.getWatchlistMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 0) { moviePageApi.getWatchlistMoviePage(any(), any(), any(), any()) }
    }

    @Test
    fun `Should retrieve watchlist page from API and store it in DB`() = runBlocking {
        val moviePage = mockk<MoviePage>()

        every { moviePageDb.getWatchlistMoviePage(1) } returns null
        every { moviePageApi.getWatchlistMoviePage(1, any(), any(), any()) } returns moviePage

        val retrieved = subject.getWatchlistMoviePage(1, mockk(), mockk(), mockk())

        assertEquals(moviePage, retrieved)
        verify(exactly = 1) { moviePageApi.getWatchlistMoviePage(any(), any(), any(), any()) }
        verify { moviePageDb.saveWatchlistMoviePage(1, moviePage) }
    }

    @Test
    fun `Should return null and not attempt to save watchlist page when API returns null`() = runBlocking {
        every { moviePageDb.getWatchlistMoviePage(1) } returns null
        every { moviePageApi.getWatchlistMoviePage(1, any(), any(), any()) } returns null

        val retrieved = subject.getWatchlistMoviePage(1, mockk(), mockk(), mockk())

        assertNull(retrieved)
        verify(exactly = 1) { moviePageApi.getWatchlistMoviePage(any(), any(), any(), any()) }
        verify(exactly = 0) { moviePageDb.saveWatchlistMoviePage(1, any()) }
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
