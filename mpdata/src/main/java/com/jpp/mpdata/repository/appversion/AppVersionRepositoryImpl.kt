package com.jpp.mpdata.repository.appversion

import com.jpp.mpdata.BuildConfig
import com.jpp.mpdomain.repository.AppVersionRepository

/**
 * [AppVersionRepository] implementation.
 *
 * Note: Untested for simplicity.
 */
class AppVersionRepositoryImpl : AppVersionRepository {
    override fun getCurrentAppVersion(): String = BuildConfig.VERSION_NAME
}