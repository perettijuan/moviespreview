package com.jpp.mpdata.repository.about

import android.content.Context
import com.jpp.mpdomain.repository.AboutNavigationRepository

/**
 * Note: Untested for simplicity.
 */
class AboutNavigationRepositoryImpl(private val context: Context) : AboutNavigationRepository {
    override fun getTheMovieDbTermOfUseUrl() = "https://www.themoviedb.org/documentation/api/terms-of-use?"
    override fun getCodeRepoUrl() = "https://github.com/perettijuan/moviespreview"
    override fun getGPlayAppUrl() = "market://details?id=${context.packageName}"
    override fun getGPlayWebUrl() = "http://play.google.com/store/apps/details?id=${context.packageName}"
    override fun getSharingUrl() = String.format("%s?personId=%s", "https://play.google.com/store/apps/details", context.packageName)
}