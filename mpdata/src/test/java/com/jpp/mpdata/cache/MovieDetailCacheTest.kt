package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.DBMovieDetail
import com.jpp.mpdata.cache.room.DBMovieGenre
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.MovieDAO
import com.jpp.mpdata.cache.room.MovieDetailDAO
import com.jpp.mpdata.cache.room.RoomDomainAdapter
import com.jpp.mpdomain.MovieDetail
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MovieDetailCacheTest {

    @RelaxedMockK
    private lateinit var movieDAO: MovieDAO
    @RelaxedMockK
    private lateinit var detailsDAO: MovieDetailDAO
    @MockK
    private lateinit var roomModelAdapter: RoomDomainAdapter
    @MockK
    private lateinit var timestampHelper: CacheTimestampHelper

    private lateinit var subject: MovieDetailCache

    @BeforeEach
    fun setUp() {
        val roomDatabase = mockk<MPRoomDataBase>()
        every { roomDatabase.moviesDao() } returns movieDAO
        every { roomDatabase.movieDetailsDao() } returns detailsDAO
        subject = MovieDetailCache(roomDatabase, roomModelAdapter, timestampHelper)
    }

    @Test
    fun `Should return null when there is no detail stored in Database`() {
        every { timestampHelper.now() } returns 12L
        every { detailsDAO.getMovieDetail(any(), any()) } returns null

        val result = subject.getMovieDetails(10.toDouble())

        Assertions.assertNull(result)
    }

    @Test
    fun `Should return null when there are no  genres for detail stored in Database`() {
        every { timestampHelper.now() } returns 12L
        every { detailsDAO.getMovieDetail(any(), any()) } returns mockk(relaxed = true)
        every { detailsDAO.getGenresForDetailId(any()) } returns null

        val result = subject.getMovieDetails(10.toDouble())

        Assertions.assertNull(result)
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

        Assertions.assertEquals(movieDetail, result)
        verify { detailsDAO.getMovieDetail(10.toDouble(), now) }
        verify { detailsDAO.getGenresForDetailId(dbMovieDetailId) }
        verify { roomModelAdapter.adaptDBMovieDetailToDataMovieDetail(dbMovieDetail, dbGenres) }
    }
}
