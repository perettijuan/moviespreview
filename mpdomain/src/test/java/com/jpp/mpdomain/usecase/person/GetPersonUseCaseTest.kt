package com.jpp.mpdomain.usecase.person

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.PersonRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetPersonUseCaseTest {

    @RelaxedMockK
    private lateinit var personRepository: PersonRepository
    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @RelaxedMockK
    private lateinit var languageRepository: LanguageRepository

    private val language = SupportedLanguage.English

    private lateinit var subject: GetPersonUseCase

    @BeforeEach
    fun setUp() {
        subject = GetPersonUseCase.Impl(personRepository, connectivityRepository, languageRepository)
        every { languageRepository.getCurrentAppLanguage() } returns language
    }


    @Test
    fun `Should check connectivity before fetching person and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.getPerson(1.toDouble()).let { result ->
            verify(exactly = 0) { personRepository.getPerson(any(), any()) }
            Assertions.assertEquals(GetPersonResult.ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { personRepository.getPerson(any(), any()) } returns null

        subject.getPerson(1.toDouble()).let { result ->
            verify(exactly = 1) { personRepository.getPerson(any(), language) }
            Assertions.assertEquals(GetPersonResult.ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected to network and an can get a person`() {
        val person = mockk<Person>()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { personRepository.getPerson(any(), any()) } returns person

        subject.getPerson(1.toDouble()).let { result ->
            verify(exactly = 1) { personRepository.getPerson(any(), any()) }
            Assertions.assertTrue(result is GetPersonResult.Success)
            Assertions.assertEquals((result as GetPersonResult.Success).person, person)
        }
    }
}