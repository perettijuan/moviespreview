package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.adapter.DomainRoomAdapter
import com.jpp.mpdata.cache.adapter.RoomDomainAdapter
import com.jpp.mpdata.cache.room.DBMovieGenre
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.MovieGenreDAO
import com.jpp.mpdomain.MovieGenre
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MovieGenreCacheTest {

    @RelaxedMockK
    private lateinit var toDomain: RoomDomainAdapter

    @RelaxedMockK
    private lateinit var toRoom: DomainRoomAdapter

    @RelaxedMockK
    private lateinit var timestamp: CacheTimestampHelper

    @RelaxedMockK
    private lateinit var movieGenresDao: MovieGenreDAO

    private val now = 12L
    private lateinit var subject: MovieGenreCache

    @BeforeEach
    fun setUp() {
        val roomDataBase = mockk<MPRoomDataBase>()

        every { roomDataBase.movieGenresDao() } returns movieGenresDao
        every { timestamp.now() } returns now

        subject = MovieGenreCache(roomDataBase, toDomain, toRoom, timestamp)
    }

    @Test
    fun `Should return null when there are no genres stored in db`() {
        every { movieGenresDao.getMovieGenres(any()) } returns null

        val result = subject.getMovieGenres()

        assertNull(result)
    }

    @Test
    fun `Should map all db genres to domain genres`() {
        every { movieGenresDao.getMovieGenres(any()) } returns dbGenres
        every { toDomain.movieGenre(dbGenre1) } returns domainGenre1
        every { toDomain.movieGenre(dbGenre2) } returns domainGenre2
        every { toDomain.movieGenre(dbGenre3) } returns domainGenre3
        every { toDomain.movieGenre(dbGenre4) } returns domainGenre4

        val result = subject.getMovieGenres()

        assertNotNull(result)
        result?.forEachIndexed { index, movieGenre ->
            assertEquals(domainGenres[index], movieGenre)
        }
    }

    @Test
    fun `Should save mapped movie genre`() {
        val capturingSlot = slot<List<DBMovieGenre>>()

        val dueDate = 14L

        every { timestamp.now() } returns 4L
        every { timestamp.movieGenresRefreshTime() } returns 10L

        every { toRoom.movieGenre(domainGenre1, dueDate) } returns dbGenre1
        every { toRoom.movieGenre(domainGenre2, dueDate) } returns dbGenre2
        every { toRoom.movieGenre(domainGenre3, dueDate) } returns dbGenre3
        every { toRoom.movieGenre(domainGenre4, dueDate) } returns dbGenre4
        every { movieGenresDao.saveMovieGenres(capture(capturingSlot)) } just Runs


        subject.saveMovieGenres(domainGenres)

        capturingSlot.captured.forEachIndexed { index, movieGenre ->
            assertEquals(dbGenres[index], movieGenre)
        }
    }

    companion object {
        private val dbGenre1 = mockk<DBMovieGenre>(relaxed = true)
        private val dbGenre2 = mockk<DBMovieGenre>(relaxed = true)
        private val dbGenre3 = mockk<DBMovieGenre>(relaxed = true)
        private val dbGenre4 = mockk<DBMovieGenre>(relaxed = true)
        private val dbGenres = listOf(
            dbGenre1,
            dbGenre2,
            dbGenre3,
            dbGenre4
        )

        private val domainGenre1 = mockk<MovieGenre>(relaxed = true)
        private val domainGenre2 = mockk<MovieGenre>(relaxed = true)
        private val domainGenre3 = mockk<MovieGenre>(relaxed = true)
        private val domainGenre4 = mockk<MovieGenre>(relaxed = true)
        private val domainGenres = listOf(domainGenre1, domainGenre2, domainGenre3, domainGenre4)
    }
}