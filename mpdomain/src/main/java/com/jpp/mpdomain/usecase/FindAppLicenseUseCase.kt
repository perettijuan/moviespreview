package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.License
import com.jpp.mpdomain.repository.LicensesRepository

/**
 * Use case to find a specific [License] given a license Id.
 */
class FindAppLicenseUseCase(private val licensesRepository: LicensesRepository) {

    suspend fun execute(licenseId: Int): Try<License> {
        val licenses =
            licensesRepository.loadLicences() ?: return Try.Failure(Try.FailureCause.Unknown)

        return try {
            licenses.licenses.first { license ->
                license.id == licenseId
            }.let { licenseFound ->
                Try.Success(licenseFound)
            }
        } catch (e: Exception) {
            Try.Failure(Try.FailureCause.Unknown)
        }
    }
}
