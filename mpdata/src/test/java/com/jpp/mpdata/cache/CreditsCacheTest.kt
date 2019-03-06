package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.*
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.CrewMember
import io.mockk.every
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
class CreditsCacheTest {

    @RelaxedMockK
    private lateinit var creditsDao: CreditsDao
    @RelaxedMockK
    private lateinit var roomModelAdapter: RoomModelAdapter
    @RelaxedMockK
    private lateinit var timestampHelper: CacheTimestampHelper

    private val now = 12L
    private lateinit var subject: CreditsCache

    @BeforeEach
    fun setUp() {
        val roomDatabase = mockk<MPRoomDataBase>()

        every { roomDatabase.creditsDao() } returns creditsDao
        every { timestampHelper.now() } returns now

        subject = CreditsCache(roomDatabase, roomModelAdapter, timestampHelper)
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
        every { roomModelAdapter.adaptDBCreditsToDomain(any(), any(), any()) } returns expectedCredits

        val result = subject.getCreditsForMovie(movieId)

        assertEquals(expectedCredits, result)
        verify { creditsDao.getMovieCastCharacters(movieId, now) }
        verify { creditsDao.getMovieCrew(movieId, now) }
        verify { roomModelAdapter.adaptDBCreditsToDomain(dbCharacters, dbCrew, movieId) }
    }

    @Test
    fun `Should store characters and crew members when storing credits`() {
        val movieId = 12.toDouble()
        val cast: List<CastCharacter> = mockk()
        val crew: List<CrewMember> = mockk()
        val dbCharacters: List<DBCastCharacter> = mockk()
        val dbCrew: List<DBCrewPerson> = mockk()
        val credits = Credits(
                id = movieId,
                cast = cast,
                crew = crew
        )

        every { roomModelAdapter.adaptDomainCastCharacterListToDB(any(), any(), any()) } returns dbCharacters
        every { roomModelAdapter.adaptDomainCrewMemberListToDB(any(), any(), any()) } returns dbCrew

        subject.storeCredits(credits)

        verify { roomModelAdapter.adaptDomainCastCharacterListToDB(cast, movieId, now) }
        verify { roomModelAdapter.adaptDomainCrewMemberListToDB(crew, movieId, now) }
        verify { creditsDao.insertCastCharacters(dbCharacters) }
        verify { creditsDao.insertCrew(dbCrew) }
    }
}