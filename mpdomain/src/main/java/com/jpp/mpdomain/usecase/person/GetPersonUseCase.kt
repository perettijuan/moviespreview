package com.jpp.mpdomain.usecase.person

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
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
     *  - [GetPersonResult.Success] when the person is found.
     *  - [GetPersonResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [GetPersonResult.ErrorUnknown] when an error occur while fetching the person.
     */
    fun getPerson(personId: Double): GetPersonResult


    class Impl(private val personRepository: PersonRepository,
               private val connectivityRepository: ConnectivityRepository,
               private val languageRepository: LanguageRepository) : GetPersonUseCase {

        override fun getPerson(personId: Double): GetPersonResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Connectivity.Disconnected -> GetPersonResult.ErrorNoConnectivity
                Connectivity.Connected -> personRepository.getPerson(personId, languageRepository.getCurrentDeviceLanguage())?.let {
                    GetPersonResult.Success(it)
                } ?: run {
                    GetPersonResult.ErrorUnknown
                }
            }
        }
    }
}