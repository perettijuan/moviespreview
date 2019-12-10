package com.jpp.mpdata.datasources.account

import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

/**
 * API definition to manipulate all the [UserAccount] data in the remote resources.
 */
interface AccountApi {
    /**
     * @return the [UserAccount] that is related to the provided [session]. Null if
     * there's no data for the provided [session] or an error is detected.
     */
    fun getUserAccountInfo(session: Session): UserAccount?
}
