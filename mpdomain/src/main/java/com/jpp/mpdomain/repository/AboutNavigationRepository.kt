package com.jpp.mpdomain.repository

interface AboutNavigationRepository {
    fun getTheMovieDbTermOfUseUrl(): String
    fun getCodeRepoUrl(): String
}