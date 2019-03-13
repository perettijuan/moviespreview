package com.jpp.mpdomain.usecase.licenses

import com.jpp.mpdomain.License

/**
 * Represents the results that can be obtained from [GetLicenseUseCase] execution.
 */
sealed class GetLicenceResult {
    object ErrorUnknown : GetLicenceResult()
    data class Success(val licence: License) : GetLicenceResult()
}