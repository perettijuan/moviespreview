package com.jpp.mpdata.cache.timestamps

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.TimeUnit

class MPTimestampsImpl(private val context: Context) : MPTimestamps {

    private sealed class TimestampId(val id: String, val refreshTime: Long) {
        object CacheAppConfiguration : TimestampId("MPTimestamps:AppConfiguration", TimeUnit.MINUTES.toMillis(30))
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

    private fun isTimestampUpToDate(timestampId: TimestampId) = with(timestampId) {
        isTimestampUpToDate(getTimestamp(this), refreshTime)
    }

    /**
     * Retrieves a Long value that represents the Timestamp provided. If the
     * local storage has no value, then null is returned.
     */
    private fun getTimestamp(id: TimestampId): Long? {
        return with(getSharedPreferences()) {
            getLong(id.id, 0).takeIf { it != 0.toLong() }
        }
    }

    /**
     * Determinate if the provided [timestamp] is outdated based on the [refreshTime].
     * Will return
     */
    private fun isTimestampUpToDate(timestamp: Long?, refreshTime: Long): Boolean {
        return timestamp?.let {
            (currentTimeInMillis() - it) < refreshTime
        } ?: false
    }

    private fun updateTimestamp(id: TimestampId, timestamp: Long) {
        with(getSharedPreferences().edit()) {
            putLong(id.id, timestamp)
            apply()
        }
    }
}