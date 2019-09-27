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
            val cast = getMovieCastCharacters(movieId, now())
            val crew = getMovieCrew(movieId, now())
            if (cast != null && cast.isNotEmpty() && crew != null && crew.isNotEmpty()) {
                transformWithAdapter { adaptDBCreditsToDomain(cast, crew, movieId) }
            } else {
                null
            }
        }
    }

    override fun storeCredits(credits: Credits) {
        withCreditsDao {
            insertCastCharacters(transformWithAdapter { adaptDomainCastCharacterListToDB(credits.cast, credits.id, creditsRefreshTime()) })
            insertCrew(transformWithAdapter { adaptDomainCrewMemberListToDB(credits.crew, credits.id, creditsRefreshTime()) })
        }
    }

    override fun clearAllData() {
        withCreditsDao {
            deleteAllCastCharacters()
            deleteAllCrew()
        }
    }

    /**
     * Helper function to execute a [transformation] in with the [RoomModelAdapter] instance.
     */
    private fun <T> transformWithAdapter(transformation: RoomModelAdapter.() -> T): T = with(adapter) { transformation.invoke(this) }

    /**
     * Helper function to execute an [action] with the [CreditsDao] instance obtained from [MPRoomDataBase].
     */
    private fun <T> withCreditsDao(action: CreditsDao.() -> T): T = with(roomDatabase.creditsDao()) { action.invoke(this) }

    /**
     * @return a Long that represents the current time.
     */
    private fun now() = timestampHelper.now()

    /**
     * @return a Long that represents the expiration date of the credits data stored in the
     * device.
     */
    private fun creditsRefreshTime() = with(timestampHelper) { now() + creditsRefreshTime() }
}