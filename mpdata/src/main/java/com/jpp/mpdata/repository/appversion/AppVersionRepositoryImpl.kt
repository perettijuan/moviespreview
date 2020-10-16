package com.jpp.mpdata.repository.appversion

import android.content.Context
import com.jpp.mpdata.BuildConfig
import com.jpp.mpdomain.AppVersion
import com.jpp.mpdomain.repository.AppVersionRepository

/**
 * [AppVersionRepository] implementation.
 *
 * Note: Untested for simplicity.
 */
class AppVersionRepositoryImpl(private val context: Context) : AppVersionRepository {
    override suspend fun getCurrentAppVersion(): AppVersion {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return AppVersion(packageInfo.versionName)
    }
}
