package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.CreditsDao
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdata.repository.credits.CreditsDb
import com.jpp.mpdomain.Credits

/**
 * [CreditsDb] implementation with a caching mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class CreditsCache(private val roomDatabase: MPRoomDataBase,
                   private val adapter: RoomModelAdapter,
                   private val timestampHelper: CacheTimestampHelper) : CreditsDb {

    override fun getCreditsForMovie(movieId: Double): Credits? {
        return withCreditsDao {
            val cast = getMovieCastCharacters(movieId, creditsRefreshTime())
            val crew = getMovieCrew(movieId, creditsRefreshTime())
            if (cast != null && crew != null) {
                transformWithAdapter { adaptDBCreditsToDomain(cast, crew, movieId) }
            } else {
                null
            }
        }
    }

    override fun storeCredits(credits: Credits) {
        withCreditsDao {
            insertCastCharacters(transformWithAdapter { adaptDomainCastCharacterListToDB(credits.cast, credits.id, now()) })
            insertCrew(transformWithAdapter { adaptDomainCrewMemberListToDB(credits.crew, credits.id, now()) })
        }
    }


    private fun <T> withCreditsDao(action: CreditsDao.() -> T): T = with(roomDatabase.creditsDao()) { action.invoke(this) }
    private fun <T> transformWithAdapter(action: RoomModelAdapter.() -> T): T = with(adapter) { action.invoke(this) }
    private fun now() = timestampHelper.now()
    private fun creditsRefreshTime() = with(timestampHelper) { now() + creditsRefreshTime() }
}