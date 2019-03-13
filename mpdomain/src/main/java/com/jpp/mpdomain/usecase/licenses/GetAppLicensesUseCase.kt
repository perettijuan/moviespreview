package com.jpp.mpdomain.usecase.licenses

import com.jpp.mpdomain.repository.LicensesRepository

/**
 * Defines a UseCase that retrieves the list of results that are used by the application.
 * UseCase definition: fetch the license list from the data storage and expose it to the upper
 * layer. If an error is detected, notify the client about it.
 */
interface GetAppLicensesUseCase {
    /**
     * Retrieves the license list that can be shown.
     * @return
     *  - [GetLicensesResult.Success] when the list of results can be retrieved.
     *  - [GetLicensesResult.ErrorUnknown] when an error is detected.
     */
    fun getAppLicences(): GetLicensesResult

    class Impl(private val licensesRepository: LicensesRepository) : GetAppLicensesUseCase {
        override fun getAppLicences(): GetLicensesResult {
            return licensesRepository.loadLicences()?.let {
                GetLicensesResult.Success(it)
            } ?: run {
                GetLicensesResult.ErrorUnknown
            }
        }
    }
}