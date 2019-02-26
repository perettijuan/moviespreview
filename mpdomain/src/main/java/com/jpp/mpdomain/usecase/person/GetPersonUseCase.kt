package com.jpp.mpdomain.usecase.person

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.PersonRepository

/**
 * Defines a UseCase that retrieves a person that is identified by an id,
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected, retrieve the person identified with personId.
 * If not connected, return an error that indicates such state.
 */
interface GetPersonUseCase {
    /**
     * Retrieves the details of a particular person identified with [personId].
     * @return
     *  - [GetPersonUseCaseResult.Success] when the person is found.
     *  - [GetPersonUseCaseResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [GetPersonUseCaseResult.ErrorUnknown] when an error occur while fetching the person.
     */
    fun getPerson(personId: Double): GetPersonUseCaseResult


    class Impl(private val personRepository: PersonRepository,
               private val connectivityRepository: ConnectivityRepository) : GetPersonUseCase {

        override fun getPerson(personId: Double): GetPersonUseCaseResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Connectivity.Disconnected -> GetPersonUseCaseResult.ErrorNoConnectivity
                Connectivity.Connected -> personRepository.getPerson(personId)?.let {
                    GetPersonUseCaseResult.Success(it)
                } ?: run {
                    GetPersonUseCaseResult.ErrorUnknown
                }
            }
        }
    }
}