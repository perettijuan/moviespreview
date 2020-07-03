package com.jpp.mpdata.repository.appversion

import com.jpp.mpdata.BuildConfig
import com.jpp.mpdomain.AppVersion
import com.jpp.mpdomain.repository.AppVersionRepository

/**
 * [AppVersionRepository] implementation.
 *
 * Note: Untested for simplicity.
 */
class AppVersionRepositoryImpl : AppVersionRepository {
    override suspend  fun getCurrentAppVersion(): AppVersion = AppVersion(BuildConfig.VERSION_NAME)
}
