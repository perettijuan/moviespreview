package com.jpp.mpdata.repository.account

import com.jpp.mpdomain.AccessToken

interface SessionApi {
    fun getAccessToken(): AccessToken?
}