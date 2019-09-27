package com.jpp.mpdata.repository.about

import android.content.Context
import com.jpp.mpdomain.AboutUrl
import com.jpp.mpdomain.repository.AboutUrlRepository

/**
 * [AboutUrlRepository] implementation.
 *
 * Note: Untested for simplicity.
 */
class AboutUrlRepositoryImpl(private val context: Context) : AboutUrlRepository {
    override fun getTheMovieDbTermOfUseUrl() = AboutUrl("https://www.themoviedb.org/documentation/api/terms-of-use?")
    override fun getCodeRepoUrl() = AboutUrl("https://github.com/perettijuan/moviespreview")
    override fun getGPlayAppUrl() = AboutUrl("market://details?id=${context.packageName}")
    override fun getGPlayWebUrl() = AboutUrl("http://play.google.com/store/apps/details?id=${context.packageName}")
    override fun getSharingUrl() = AboutUrl(String.format("%s?personId=%s", "https://play.google.com/store/apps/details", context.packageName))
    override fun getPrivacyPolicyUrl() = AboutUrl("https://perettijuan.wixsite.com/moviespreview")
}
