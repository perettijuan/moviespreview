package com.jpp.mpdomain.repository

interface AboutNavigationRepository {
    fun getTheMovieDbTermOfUseUrl(): String
    fun getCodeRepoUrl(): String
    fun getGPlayAppUrl(): String
    fun getGPlayWebUrl(): String
    fun getSharingUrl(): String
}