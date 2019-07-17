package com.jpp.mpdata.cache.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CreditsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCastCharacters(castCharacters: List<DBCastCharacter>)

    @Query("select * from movie_cast_characters where movie_id = :movieId and duedate >= :nowDate")
    fun getMovieCastCharacters(movieId: Double, nowDate: Long): List<DBCastCharacter>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrew(castCharacters: List<DBCrewPerson>)

    @Query("select * from movie_crew_person where movie_id = :movieId and duedate >= :nowDate")
    fun getMovieCrew(movieId: Double, nowDate: Long): List<DBCrewPerson>?

    @Query("DELETE FROM movie_cast_characters")
    fun deleteAllCastCharacters()

    @Query("DELETE FROM movie_crew_person")
    fun deleteAllCrew()
}