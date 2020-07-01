package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel

/**
 * Repository that handles all [Session] related data.
 */
@ExperimentalCoroutinesApi
interface SessionRepository {

    /**
     * Subscribe to this channel in order to receive updates when the [Session]
     * is affected some-how.
     *
     * Reminder: decided to use a Channel instead of a Flow because Flows are
     * 'cold' in the sense that data is lost if no consumer is attached
     * to the Flow.
     *
     * Reminder 2: decided to use a [BroadcastChannel] because a common [Channel]
     * blocks the coroutine (like if the thread was blocked) and I want to implement
     * a fire and forget mechanism.
     */
    suspend fun sessionStateUpdates(): BroadcastChannel<Session?>

    /**
     * @return the current [Session] being used - if any.
     */
    suspend fun getCurrentSession(): Session?

    /**
     * Deletes the current [Session] being used - if any.
     */
    suspend fun deleteCurrentSession()

    /**
     * Creates a new [Session] for the provided [AccessToken].
     */
    suspend fun createSession(accessToken: AccessToken): Session?
}
