package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomDomainAdapter
import com.jpp.mpdata.repository.credits.CreditsDb
import com.jpp.mpdomain.Credits

/**
 * [CreditsDb] implementation with a caching mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class CreditsCache(
    roomDatabase: MPRoomDataBase,
    private val toDomain: RoomDomainAdapter,
    private val toRoom: DomainRoomAdapter,
    private val timestamp: CacheTimestampHelper
) : CreditsDb {

    private val creditsDao = roomDatabase.creditsDao()

    override fun getCreditsForMovie(movieId: Double): Credits? {
        val dbCast = creditsDao.getMovieCastCharacters(movieId, timestamp.now()) ?: return null
        val dbCrew = creditsDao.getMovieCrew(movieId, timestamp.now()) ?: return null
        return if (dbCast.isNotEmpty() && dbCrew.isNotEmpty()) {
            toDomain.credits(dbCast, dbCrew, movieId)
        } else {
            null
        }
    }

    override fun storeCredits(credits: Credits) {
        val dbCharacters = credits.cast.map { domainCharacter ->
            toRoom.castCharacter(domainCharacter, credits.id, timestamp.creditsDueDate())
        }
        val dbCrew = credits.crew.map { crewMember ->
            toRoom.crewPerson(crewMember, credits.id, timestamp.creditsDueDate())
        }
        creditsDao.insertCastCharacters(dbCharacters)
        creditsDao.insertCrew(dbCrew)
    }

    override fun clearAllData() {
        creditsDao.deleteAllCastCharacters()
        creditsDao.deleteAllCrew()
    }

    /**
     * @return a Long that represents the expiration date of the credits data stored in the
     * device.
     */
    private fun CacheTimestampHelper.creditsDueDate() = now() + creditsRefreshTime()
}
