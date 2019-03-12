package com.jpp.mpdomain.usecase.licenses

import com.jpp.mpdomain.repository.LicensesRepository

/**
 * Defines a UseCase that retrieves a particular license.
 * UseCase definition: fetch the license requested from the data storage and expose it to the upper
 * layer. If an error is detected, notify the client about it.
 */
interface GetLicenseUseCase {
    /**
     * Retrieves the license identified by [licId].
     * @return
     *  - [GetLicenceResult.Success] when the license is retrieved.
     *  - [GetLicenceResult.ErrorUnknown] when an error is detected.
     */
    fun getLicense(licId: Int): GetLicenceResult

    class Impl(private val licensesRepository: LicensesRepository) : GetLicenseUseCase {
        override fun getLicense(licId: Int): GetLicenceResult {
            return licensesRepository.loadLicences()?.let {
                try {
                    it.licenses
                            .first { license -> license.id == licId }
                            .let { found -> GetLicenceResult.Success(found) }
                } catch (e: NoSuchElementException) {
                    GetLicenceResult.ErrorUnknown
                }
            } ?: run {
                GetLicenceResult.ErrorUnknown
            }
        }
    }
}