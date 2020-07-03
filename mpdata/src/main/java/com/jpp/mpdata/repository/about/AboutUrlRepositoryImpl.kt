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
    override suspend fun getTheMovieDbTermOfUseUrl() = AboutUrl("https://www.themoviedb.org/documentation/api/terms-of-use?")
    override suspend fun getCodeRepoUrl() = AboutUrl("https://github.com/perettijuan/moviespreview")
    override suspend fun getGPlayAppUrl() = AboutUrl("market://details?id=${context.packageName}")
    override suspend fun getGPlayWebUrl() = AboutUrl("http://play.google.com/store/apps/details?id=${context.packageName}")
    override suspend fun getSharingUrl() = AboutUrl(String.format("%s?personId=%s", "https://play.google.com/store/apps/details", context.packageName))
    override suspend fun getPrivacyPolicyUrl() = AboutUrl("https://perettijuan.wixsite.com/moviespreview")
}
