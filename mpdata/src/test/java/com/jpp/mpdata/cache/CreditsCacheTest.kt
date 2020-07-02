package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.adapter.DomainRoomAdapter
import com.jpp.mpdata.cache.adapter.RoomDomainAdapter
import com.jpp.mpdata.cache.room.CreditsDao
import com.jpp.mpdata.cache.room.DBCastCharacter
import com.jpp.mpdata.cache.room.DBCrewPerson
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdomain.Credits
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CreditsCacheTest {

    @RelaxedMockK
    private lateinit var creditsDao: CreditsDao
    @RelaxedMockK
    private lateinit var toDomain: RoomDomainAdapter
    @RelaxedMockK
    private lateinit var toRoom: DomainRoomAdapter
    @RelaxedMockK
    private lateinit var timestampHelper: CacheTimestampHelper

    private val now = 12L
    private lateinit var subject: CreditsCache

    @BeforeEach
    fun setUp() {
        val roomDatabase = mockk<MPRoomDataBase>()

        every { roomDatabase.creditsDao() } returns creditsDao
        every { timestampHelper.now() } returns now

        subject = CreditsCache(roomDatabase, toDomain, toRoom, timestampHelper)
    }

    @Test
    fun `Should return null when there is no cast stored in Database`() {
        every { creditsDao.getMovieCastCharacters(any(), any()) } returns null
        every { creditsDao.getMovieCrew(any(), any()) } returns mockk(relaxed = true)

        val result = subject.getCreditsForMovie(12.toDouble())

        assertNull(result)
    }

    @Test
    fun `Should return null when there is no crew stored in Database`() {
        every { creditsDao.getMovieCastCharacters(any(), any()) } returns mockk(relaxed = true)
        every { creditsDao.getMovieCrew(any(), any()) } returns null

        val result = subject.getCreditsForMovie(12.toDouble())

        assertNull(result)
    }

    @Test
    fun `Should return null when there is no data stored in Database`() {
        every { creditsDao.getMovieCastCharacters(any(), any()) } returns null
        every { creditsDao.getMovieCrew(any(), any()) } returns null

        val result = subject.getCreditsForMovie(12.toDouble())

        assertNull(result)
    }

    @Test
    fun `Should return mapped credits`() {
        val dbCharacters: List<DBCastCharacter> = mockk()
        val dbCrew: List<DBCrewPerson> = mockk()
        val expectedCredits: Credits = mockk()
        val movieId = 12.toDouble()

        every { dbCharacters.isEmpty() } returns false
        every { dbCrew.isEmpty() } returns false
        every { timestampHelper.creditsRefreshTime() } returns 1
        every { creditsDao.getMovieCastCharacters(any(), any()) } returns dbCharacters
        every { creditsDao.getMovieCrew(any(), any()) } returns dbCrew
        every { toDomain.credits(any(), any(), any()) } returns expectedCredits

        val result = subject.getCreditsForMovie(movieId)

        assertEquals(expectedCredits, result)
    }
}
