package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AboutUrl

interface AboutUrlRepository {
    fun getTheMovieDbTermOfUseUrl(): AboutUrl
    fun getCodeRepoUrl(): AboutUrl
    fun getGPlayAppUrl(): AboutUrl
    fun getGPlayWebUrl(): AboutUrl
    fun getSharingUrl(): AboutUrl
    fun getPrivacyPolicyUrl() : AboutUrl
}