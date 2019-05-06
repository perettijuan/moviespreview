package com.jpp.mpdata.repository.session

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session

interface SessionApi {
    @Deprecated("Use AccessTokenApi instead")
    fun getAccessToken(): AccessToken?
    fun createSession(accessToken: AccessToken): Session?
}