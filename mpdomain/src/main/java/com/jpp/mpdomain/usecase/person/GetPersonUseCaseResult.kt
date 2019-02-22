package com.jpp.mpdomain.usecase.person

import com.jpp.mpdomain.Person


/**
 * Represents the result of the execution if [GetPersonUseCase].
 */
sealed class GetPersonUseCaseResult {
    object ErrorNoConnectivity : GetPersonUseCaseResult()
    object ErrorUnknown : GetPersonUseCaseResult()
    data class Success(val person: Person) : GetPersonUseCaseResult()
}