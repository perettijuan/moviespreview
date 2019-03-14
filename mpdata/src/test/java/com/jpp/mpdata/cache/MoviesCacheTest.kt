package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.*
import com.jpp.mpdomain.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MoviesCacheTest {

    @RelaxedMockK
    private lateinit var movieDAO: MovieDAO
    @RelaxedMockK
    private lateinit var detailsDAO: MovieDetailDAO
    @MockK
    private lateinit var roomModelAdapter: RoomModelAdapter
    @MockK
    private lateinit var timestampHelper: CacheTimestampHelper

    private lateinit var subject: MoviesCache
    private val page: Int = 1
    private val section = MovieSection.Playing

    @BeforeEach
    fun setUp() {
        val roomDatabase = mockk<MPRoomDataBase>()
        every { roomDatabase.moviesDao() } returns movieDAO
        every { roomDatabase.movieDetailsDao() } returns detailsDAO
        subject = MoviesCache(roomDatabase, roomModelAdapter, timestampHelper)
    }

    @Test
    fun `Should return null when there is no movie page stored in Database`() {
        val now = 12L

        every { timestampHelper.now() } returns now
        every { movieDAO.getMoviePage(page, section.name, now, any()) } returns null

        val result = subject.getMoviePageForSection(page, section, SupportedLanguage.English)

        assertNull(result)
    }

    @Test
    fun `Should return null when there are no movies stored for the given page`() {
        val now = 12L
        val moviePage = mockk<DBMoviePage>(relaxed = true)

        every { timestampHelper.now() } returns now
        every { movieDAO.getMoviePage(page, section.name, now, any()) } returns moviePage
        every { movieDAO.getMoviesFromPage(moviePage.id) } returns null

        val result = subject.getMoviePageForSection(page, section, SupportedLanguage.English)

        assertNull(result)
        verify { movieDAO.getMoviesFromPage(moviePage.id) }
    }


    @Test
    fun `Should return mapped movie page`() {
        val now = 12L
        val dbMoviePage = mockk<DBMoviePage>(relaxed = true)
        val dbMovieList = mockk<List<DBMovie>>()
        val mappedMoviePage = mockk<MoviePage>()

        every { timestampHelper.now() } returns now
        every { movieDAO.getMoviePage(page, section.name, now, SupportedLanguage.English.id) } returns dbMoviePage
        every { movieDAO.getMoviesFromPage(dbMoviePage.id) } returns dbMovieList
        every { roomModelAdapter.adaptDBMoviePageToDataMoviePage(dbMoviePage, dbMovieList) } returns mappedMoviePage

        val result = subject.getMoviePageForSection(page, section, SupportedLanguage.English)

        assertEquals(result, mappedMoviePage)
    }

    @Test
    fun `Should insert movie page and movie list when saving movie page`() {
        val moviePage = mockk<MoviePage>()
        val movieList = listOf<Movie>(mockk(), mockk(), mockk())
        val dbMoviePage = mockk<DBMoviePage>(relaxed = true)
        val now = 12L
        val movieRefreshTime = 10L
        val expectedDueDate = 22L
        val insertedMoviePageId = 1L

        every { moviePage.results } returns movieList
        every { timestampHelper.now() } returns now
        every { timestampHelper.moviePagesRefreshTime() } returns movieRefreshTime
        every { roomModelAdapter.adaptDataMoviePageToDBMoviePage(any(), any(), any(), SupportedLanguage.English.id) } returns dbMoviePage
        every { roomModelAdapter.adaptDataMovieToDBMovie(any(), any()) } returns mockk()
        every { movieDAO.insertMoviePage(any()) } returns insertedMoviePageId

        subject.saveMoviePageForSection(moviePage, section, SupportedLanguage.English)


        verify { roomModelAdapter.adaptDataMoviePageToDBMoviePage(moviePage, section.name, expectedDueDate, SupportedLanguage.English.id) }
        verify(exactly = 3) { roomModelAdapter.adaptDataMovieToDBMovie(any(), insertedMoviePageId) }
        verify { movieDAO.insertMoviePage(dbMoviePage) }
        verify { movieDAO.insertMovies(any()) }
    }

    @Test
    fun `Should return null when there is no detail stored in Database`() {
        every { timestampHelper.now() } returns 12L
        every { detailsDAO.getMovieDetail(any(), any()) } returns null

        val result = subject.getMovieDetails(10.toDouble())

        assertNull(result)
    }

    @Test
    fun `Should return null when there are no  genres for detail stored in Database`() {
        every { timestampHelper.now() } returns 12L
        every { detailsDAO.getMovieDetail(any(), any()) } returns mockk(relaxed = true)
        every { detailsDAO.getGenresForDetailId(any()) } returns null

        val result = subject.getMovieDetails(10.toDouble())

        assertNull(result)
    }

    @Test
    fun `Should return mapped details`() {
        val dbMovieDetailId = 17.toDouble()
        val dbMovieDetail = mockk<DBMovieDetail>()
        val dbGenres = listOf<DBMovieGenre>(mockk(), mockk())
        val movieDetail = mockk<MovieDetail>()
        val now = 12L

        every { dbMovieDetail.id } returns dbMovieDetailId
        every { timestampHelper.now() } returns now
        every { detailsDAO.getMovieDetail(any(), any()) } returns dbMovieDetail
        every { detailsDAO.getGenresForDetailId(any()) } returns dbGenres
        every { roomModelAdapter.adaptDBMovieDetailToDataMovieDetail(any(), any()) } returns movieDetail

        val result = subject.getMovieDetails(10.toDouble())

        assertEquals(movieDetail, result)
        verify { detailsDAO.getMovieDetail(10.toDouble(), now) }
        verify { detailsDAO.getGenresForDetailId(dbMovieDetailId) }
        verify { roomModelAdapter.adaptDBMovieDetailToDataMovieDetail(dbMovieDetail, dbGenres) }
    }

}