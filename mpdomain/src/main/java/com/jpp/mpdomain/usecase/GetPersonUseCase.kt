package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.PersonRepository

/**
 * Use case to retrieve a [Person] data.
 */
class GetPersonUseCase(
    private val personRepository: PersonRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val languageRepository: LanguageRepository
) {

    suspend fun execute(personId: Double): Try<Person> {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> Try.Failure(Try.FailureCause.NoConnectivity)
            is Connectivity.Connected -> personRepository.getPerson(
                personId,
                languageRepository.getCurrentAppLanguage()
            )?.let { person ->
                Try.Success(person)
            } ?: Try.Failure(Try.FailureCause.Unknown)
        }
    }
}
