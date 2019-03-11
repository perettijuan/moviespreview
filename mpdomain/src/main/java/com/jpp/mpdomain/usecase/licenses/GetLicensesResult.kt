package com.jpp.mpdomain.usecase.licenses

import com.jpp.mpdomain.Licenses

/**
 * Represents the results that can be obtained from [GetAppLicensesUseCase] execution.
 */
sealed class GetLicensesResult {
    object ErrorUnknown : GetLicensesResult()
    data class Success(val licenses: Licenses) : GetLicensesResult()
}