package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.PersonRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetPersonUseCaseTest {

    @MockK
    private lateinit var personRepository: PersonRepository

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    @MockK
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: GetPersonUseCase

    @BeforeEach
    fun setUp() {
        subject = GetPersonUseCase(personRepository, connectivityRepository, languageRepository)
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { personRepository.getPerson(any(), any()) } returns null

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should retrieve person data`() = runBlocking {
        val person = Person(
            id = 12.0,
            biography = "aBio",
            birthday = null,
            deathday = null,
            name = "aName",
            place_of_birth = null
        )

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        coEvery { personRepository.getPerson(12.0, SupportedLanguage.English) } returns person

        val actual = subject.execute(12.0)

        assertTrue(actual is Try.Success)
        assertEquals(person, actual.getOrNull())
    }
}
