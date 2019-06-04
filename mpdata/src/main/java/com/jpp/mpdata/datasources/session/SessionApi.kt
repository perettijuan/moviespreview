package com.jpp.mpdata.datasources.session

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session

interface SessionApi {
    fun createSession(accessToken: AccessToken): Session?
}