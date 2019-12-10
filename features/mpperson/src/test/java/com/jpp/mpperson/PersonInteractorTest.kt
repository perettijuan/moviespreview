package com.jpp.mpperson

import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.PersonRepository
import com.jpp.mpperson.PersonInteractor.PersonEvent.AppLanguageChanged
import com.jpp.mpperson.PersonInteractor.PersonEvent.NotConnectedToNetwork
import com.jpp.mpperson.PersonInteractor.PersonEvent.Success
import com.jpp.mpperson.PersonInteractor.PersonEvent.UnknownError
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class PersonInteractorTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @MockK
    private lateinit var personRepository: PersonRepository
    @MockK
    private lateinit var languageRepository: LanguageRepository

    private val languageUpdates = MutableLiveData<SupportedLanguage>()

    private lateinit var subject: PersonInteractor

    @BeforeEach
    fun setUp() {
        every { languageRepository.updates() } returns languageUpdates
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        subject = PersonInteractor(
                connectivityRepository,
                personRepository,
                languageRepository
        )

        /*
         * Since the ViewModel uses a MediatorLiveData, we need to have
         * an observer on the view states attached all the time in order
         * to get notifications.
         */
        subject.events.observeForever { }
    }

    @Test
    fun `Should post not connected event when not connected to network`() {
        var eventPosted: PersonInteractor.PersonEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.events.observeWith { eventPosted = it }

        subject.fetchPerson(12.0)

        assertEquals(NotConnectedToNetwork, eventPosted)
        verify(exactly = 0) { personRepository.getPerson(12.0, SupportedLanguage.English) }
    }

    @Test
    fun `Should post error unknown event when connected to network but fails to fetch person data`() {
        var eventPosted: PersonInteractor.PersonEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { personRepository.getPerson(any(), any()) } returns null

        subject.events.observeWith { eventPosted = it }

        subject.fetchPerson(12.0)

        assertEquals(UnknownError, eventPosted)
        verify { personRepository.getPerson(12.0, SupportedLanguage.English) }
    }

    @Test
    fun `Should success when fetches person data`() {
        var eventPosted: PersonInteractor.PersonEvent? = null
        val person = mockk<Person>()
        val expected = Success(person)

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { personRepository.getPerson(any(), any()) } returns person

        subject.events.observeWith { eventPosted = it }

        subject.fetchPerson(12.0)

        assertEquals(expected, eventPosted)
        verify { personRepository.getPerson(12.0, SupportedLanguage.English) }
    }

    @Test
    fun `Should post AppLanguageChanged when language changes`() {
        var eventPosted: PersonInteractor.PersonEvent? = null

        subject.events.observeWith { eventPosted = it }

        languageUpdates.value = SupportedLanguage.Spanish

        assertEquals(AppLanguageChanged, eventPosted)
    }
}
