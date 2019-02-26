package com.jpp.mpdomain.usecase.person

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.repository.ConnectivityRepository
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

    private lateinit var subject: GetPersonUseCase

    @BeforeEach
    fun setUp() {
        subject = GetPersonUseCase.Impl(personRepository, connectivityRepository)
    }


    @Test
    fun `Should check connectivity before fetching person and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.getPerson(1.toDouble()).let { result ->
            verify(exactly = 0) { personRepository.getPerson(any()) }
            Assertions.assertEquals(GetPersonUseCaseResult.ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { personRepository.getPerson(any()) } returns null

        subject.getPerson(1.toDouble()).let { result ->
            verify(exactly = 1) { personRepository.getPerson(any()) }
            Assertions.assertEquals(GetPersonUseCaseResult.ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected to network and an can get a person`() {
        val person = mockk<Person>()
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { personRepository.getPerson(any()) } returns person

        subject.getPerson(1.toDouble()).let { result ->
            verify(exactly = 1) { personRepository.getPerson(any()) }
            Assertions.assertTrue(result is GetPersonUseCaseResult.Success)
            Assertions.assertEquals((result as GetPersonUseCaseResult.Success).person, person)
        }
    }
}