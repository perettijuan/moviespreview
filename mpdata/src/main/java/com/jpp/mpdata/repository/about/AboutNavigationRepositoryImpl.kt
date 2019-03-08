package com.jpp.mpdata.repository.about

import com.jpp.mpdomain.repository.AboutNavigationRepository

class AboutNavigationRepositoryImpl : AboutNavigationRepository {
    override fun getTheMovieDbTermOfUseUrl() = "https://www.themoviedb.org/documentation/api/terms-of-use?"
    override fun getCodeRepoUrl() = "https://github.com/perettijuan/moviespreview"
}