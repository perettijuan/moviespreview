package com.jpp.mpdomain.repository

/**
 * General purpose repository to query/save data that is support for the application's behavior.
 */
interface SupportRepository {
    suspend fun clearAllData()
}
