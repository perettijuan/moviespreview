package com.jpp.mpcredits

import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.CreditsRepository
import com.jpp.mpdomain.repository.LanguageRepository
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
class CreditsInteractorTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @MockK
    private lateinit var creditsRepository: CreditsRepository
    @MockK
    private lateinit var languageRepository: LanguageRepository

    private val languageUpdates by lazy { MutableLiveData<SupportedLanguage>() }

    private lateinit var subject: CreditsInteractor

    @BeforeEach
    fun setUp() {
        every { languageRepository.updates() } returns languageUpdates
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        subject = CreditsInteractor(
                connectivityRepository,
                creditsRepository,
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
        var eventPosted: CreditsInteractor.CreditsEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.events.observeWith { eventPosted = it }

        subject.fetchCreditsForMovie(12.0)

        assertEquals(CreditsInteractor.CreditsEvent.NotConnectedToNetwork, eventPosted)
        verify(exactly = 0) { creditsRepository.getCreditsForMovie(any()) }
    }

    @Test
    fun `Should post error unknown event when connected to network but fails to fetch credits`() {
        var eventPosted: CreditsInteractor.CreditsEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { creditsRepository.getCreditsForMovie(any()) } returns null

        subject.events.observeWith { eventPosted = it }

        subject.fetchCreditsForMovie(12.0)

        assertEquals(CreditsInteractor.CreditsEvent.UnknownError, eventPosted)
        verify { creditsRepository.getCreditsForMovie(12.0) }
    }

    @Test
    fun `Should success when fetches credits`() {
        var eventPosted: CreditsInteractor.CreditsEvent? = null
        val credits = mockk<Credits>()
        val expected = CreditsInteractor.CreditsEvent.Success(credits)

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { creditsRepository.getCreditsForMovie(any()) } returns credits

        subject.events.observeWith { eventPosted = it }

        subject.fetchCreditsForMovie(12.0)

        assertEquals(expected, eventPosted)
        verify { creditsRepository.getCreditsForMovie(12.0) }
    }

    @Test
    fun `Should post AppLanguageChanged when language changes`() {
        var eventPosted: CreditsInteractor.CreditsEvent? = null

        subject.events.observeWith { eventPosted = it }

        languageUpdates.value = SupportedLanguage.Spanish

        assertEquals(CreditsInteractor.CreditsEvent.AppLanguageChanged, eventPosted)
    }
}