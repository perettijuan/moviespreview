package com.jpp.mpdomain.usecase.person

import com.jpp.mpdomain.Person


/**
 * Represents the result of the execution if [GetPersonUseCase].
 */
sealed class GetPersonResult {
    object ErrorNoConnectivity : GetPersonResult()
    object ErrorUnknown : GetPersonResult()
    data class Success(val person: Person) : GetPersonResult()
}