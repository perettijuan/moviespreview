package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Licenses
import com.jpp.mpdomain.repository.LicensesRepository

/**
 * Use case to retrieve the apps [Licenses].
 */
class GetAppLicensesUseCase(private val licensesRepository: LicensesRepository) {
    suspend fun execute(): Try<Licenses> {
        return when (val licenses = licensesRepository.loadLicences()) {
            null -> Try.Failure(Try.FailureCause.Unknown)
            else -> Try.Success(licenses)
        }
    }
}
