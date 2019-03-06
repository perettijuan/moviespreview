package com.jpp.mpdomain.usecase.credits

import com.jpp.mpdomain.Credits

/**
 * Represents the results that can be obtained from [GetCreditsUseCase] execution.
 */
sealed class GetCreditsResult {
    object ErrorNoConnectivity : GetCreditsResult()
    object ErrorUnknown : GetCreditsResult()
    data class Success(val credits: Credits) : GetCreditsResult()
}