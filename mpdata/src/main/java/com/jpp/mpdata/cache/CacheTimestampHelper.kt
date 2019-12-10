package com.jpp.mpdata.cache

import java.util.concurrent.TimeUnit

/**
 * Helper class to provide date based tools for the caching system.
 * The main motivation of the class is to be able to unit-test the cache
 * classes independently of the system.
 */
class CacheTimestampHelper {

    /**
     * @return a Long value that represents the current time in the system as milliseconds.
     */
    fun now(): Long = System.currentTimeMillis()

    /**
     * @return a Long value that represents the maximum refresh time for all the movie
     * pages stored in the device.
     */
    fun moviePagesRefreshTime(): Long = TimeUnit.MINUTES.toMillis(30)

    /**
     * @return a Long value that represents the maximum refresh time for the app configuration
     * data.
     */
    fun appConfigRefreshTime(): Long = TimeUnit.DAYS.toMillis(7)

    /**
     * @return a Long value that represents the maximum refresh time for the movie details
     * stored in the application.
     */
    fun movieDetailsRefreshTime(): Long = TimeUnit.MINUTES.toMillis(30)

    /**
     * @return a Long value that represents the maximum refresh time for the credits of a movie
     * stored in the application.
     */
    fun creditsRefreshTime(): Long = TimeUnit.MINUTES.toMillis(30)
}
