package com.jpp.mpdata.repository.person

import com.jpp.mpdomain.Person
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.PersonRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PersonRepositoryTest {

    @RelaxedMockK
    private lateinit var personApi: PersonApi
    @RelaxedMockK
    private lateinit var personDb: PersonDb

    private val personId = 22.toDouble()
    private lateinit var subject: PersonRepository

    @BeforeEach
    fun setUp() {
        subject = PersonRepositoryImpl(personApi, personDb)
    }

    @Test
    fun `Should never get from API when data is in cache`() = runBlocking {
        every { personDb.getPerson(any()) } returns mockk()

        subject.getPerson(personId, SupportedLanguage.English)

        verify { personDb.getPerson(personId) }
        verify(exactly = 0) { personApi.getPerson(any(), SupportedLanguage.English) }
        verify(exactly = 0) { personDb.savePerson(any()) }
    }

    @Test
    fun `Should update cache when data retrieved from API`() = runBlocking {
        val person = mockk<Person>()
        every { personDb.getPerson(any()) } returns null
        every { personApi.getPerson(any(), any()) } returns person

        subject.getPerson(personId, SupportedLanguage.English)

        verify { personApi.getPerson(personId, SupportedLanguage.English) }
        verify { personDb.savePerson(person) }
    }

    @Test
    fun `Should return null when it fails`() = runBlocking {
        every { personDb.getPerson(any()) } returns null
        every { personApi.getPerson(any(), any()) } returns null

        val result = subject.getPerson(personId, SupportedLanguage.English)

        verify { personApi.getPerson(personId, SupportedLanguage.English) }
        verify { personDb.getPerson(personId) }
        assertNull(result)
    }
}
