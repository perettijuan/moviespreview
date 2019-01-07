package com.jpp.mpdata.cache.timestamps

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.TimeUnit

/**
 * [MPTimestamps] implementation: it uses [SharedPreferences] to respect the contract defined by
 * the interface.
 */
class MPTimestampsImpl(private val context: Context) : MPTimestamps {

    /**
     * Represents the timestamp identifier of a given entity.
     * @property id the string that identifies the timestamp.
     * @property refreshTime the time that should be used to determinate if the data is outdated.
     */
    private sealed class TimestampId(val id: String, val refreshTime: Long) {
        object CacheAppConfiguration : TimestampId("MPTimestamps:AppConfiguration", TimeUnit.DAYS.toMillis(7))
        object CacheMoviePagePage : TimestampId("MPTimestamps:CacheMovies", TimeUnit.MINUTES.toMillis(30))
    }

    override fun isAppConfigurationUpToDate(): Boolean = isTimestampUpToDate(TimestampId.CacheAppConfiguration)

    override fun updateAppConfigurationInserted() {
        updateTimestamp(TimestampId.CacheAppConfiguration, currentTimeInMillis())
    }

    override fun updateMoviePageInserted() {
        updateTimestamp(TimestampId.CacheMoviePagePage, currentTimeInMillis())
    }

    override fun isMoviePageUpToDate(): Boolean = isTimestampUpToDate(TimestampId.CacheMoviePagePage)


    private fun getSharedPreferences(): SharedPreferences = context.getSharedPreferences("mp_cache", Context.MODE_PRIVATE)

    /**
     * Wrap currentTimeInMillis method.
     */
    private fun currentTimeInMillis() = System.currentTimeMillis()

    /**
     * @return true if the provided [timestampId] is up to date, false any other case.
     */
    private fun isTimestampUpToDate(timestampId: TimestampId) = with(timestampId) {
        isTimestampUpToDate(getTimestampValue(this), refreshTime)
    }

    /**
     * @return a Long that contains the date in which the given [TimestampId] has been
     * updated for the last time. If no value is stored for the [TimestampId], null is
     * returned.
     */
    private fun getTimestampValue(id: TimestampId): Long? {
        return with(getSharedPreferences()) {
            getLong(id.id, 0).takeIf { it != 0.toLong() }
        }
    }

    /**
     * Determinate if the provided [timestampValue] is outdated based on the [refreshTime].
     * @return a Boolean that is true when the timestampValue is up to date, false when it is not.
     */
    private fun isTimestampUpToDate(timestampValue: Long?, refreshTime: Long): Boolean {
        return timestampValue?.let {
            (currentTimeInMillis() - it) < refreshTime
        } ?: false
    }

    /**
     * Stores a Long value ([timestamp]) that represents the date in which the entity
     * associated to [TimestampId] has been updated for the last time.
     */
    private fun updateTimestamp(id: TimestampId, timestamp: Long) {
        with(getSharedPreferences().edit()) {
            putLong(id.id, timestamp)
            apply()
        }
    }
}