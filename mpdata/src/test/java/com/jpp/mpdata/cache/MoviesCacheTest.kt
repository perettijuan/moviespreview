package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.adapter.DomainRoomAdapter
import com.jpp.mpdata.cache.adapter.RoomDomainAdapter
import com.jpp.mpdata.cache.room.DBMovie
import com.jpp.mpdata.cache.room.DBMoviePage
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.MovieDAO
import com.jpp.mpdata.cache.room.MovieDetailDAO
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
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
    private lateinit var toDomain: RoomDomainAdapter

    @MockK
    private lateinit var toRoom: DomainRoomAdapter

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
        subject = MoviesCache(roomDatabase, toDomain, toRoom, timestampHelper)
    }

    @Test
    fun `Should return null when there is no movie page stored in Database`() {
        val now = 12L

        every { timestampHelper.now() } returns now
        every { movieDAO.getMoviePage(page, section.name, now) } returns null

        val result = subject.getMoviePageForSection(page, section)

        assertNull(result)
    }

    @Test
    fun `Should return null when there are no movies stored for the given page`() {
        val now = 12L
        val moviePage = mockk<DBMoviePage>(relaxed = true)

        every { timestampHelper.now() } returns now
        every { movieDAO.getMoviePage(page, section.name, now) } returns moviePage
        every { movieDAO.getMoviesFromPage(moviePage.id) } returns null

        val result = subject.getMoviePageForSection(page, section)

        assertNull(result)
    }

    @Test
    fun `Should return mapped movie page`() {
        val now = 12L
        val dbMoviePage = mockk<DBMoviePage>(relaxed = true)
        val dbMovieList = mockk<List<DBMovie>>()
        val mappedMoviePage = mockk<MoviePage>()

        every { timestampHelper.now() } returns now
        every { movieDAO.getMoviePage(page, section.name, now) } returns dbMoviePage
        every { movieDAO.getMoviesFromPage(dbMoviePage.id) } returns dbMovieList
        every { toDomain.moviePage(dbMoviePage, dbMovieList) } returns mappedMoviePage

        val result = subject.getMoviePageForSection(page, section)

        assertEquals(result, mappedMoviePage)
    }

    @Test
    fun `Should insert movie page and movie list when saving movie page`() {
        val moviePage = mockk<MoviePage>()
        val movieList = listOf<Movie>(mockk(), mockk(), mockk())
        val dbMoviePage = mockk<DBMoviePage>(relaxed = true)
        val now = 12L
        val movieRefreshTime = 10L
        val insertedMoviePageId = 1L

        every { moviePage.results } returns movieList
        every { timestampHelper.now() } returns now
        every { timestampHelper.moviePagesRefreshTime() } returns movieRefreshTime
        every {
            toRoom.moviePage(
                any(),
                any(),
                any()
            )
        } returns dbMoviePage
        every { toRoom.movie(any(), any()) } returns mockk()
        every { movieDAO.insertMoviePage(any()) } returns insertedMoviePageId

        subject.saveMoviePageForSection(moviePage, section)

        verify { movieDAO.insertMoviePage(dbMoviePage) }
        verify { movieDAO.insertMovies(any()) }
    }
}
