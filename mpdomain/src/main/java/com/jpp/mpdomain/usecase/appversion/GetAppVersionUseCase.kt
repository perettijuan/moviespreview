package com.jpp.mpdomain.usecase.appversion

import com.jpp.mpdomain.repository.AppVersionRepository

/**
 * Use case definition to fetch the current version of the application.
 */
interface GetAppVersionUseCase {
    /**
     * @return a String object that represents the current version of the application.
     */
    fun getCurrentAppVersion(): String

    class Impl(private val repository: AppVersionRepository) : GetAppVersionUseCase {
        override fun getCurrentAppVersion(): String {
            return repository.getCurrentAppVersion()
        }
    }
}