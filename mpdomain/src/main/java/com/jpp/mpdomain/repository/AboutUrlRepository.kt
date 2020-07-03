package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AboutUrl

interface AboutUrlRepository {
    suspend fun getTheMovieDbTermOfUseUrl(): AboutUrl
    suspend fun getCodeRepoUrl(): AboutUrl
    suspend fun getGPlayAppUrl(): AboutUrl
    suspend fun getGPlayWebUrl(): AboutUrl
    suspend fun getSharingUrl(): AboutUrl
    suspend fun getPrivacyPolicyUrl(): AboutUrl
}
