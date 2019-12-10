package com.jpp.mpsearch

import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.SearchRepository
import com.jpp.mpsearch.SearchInteractor.SearchEvent
import com.jpp.mpsearch.SearchInteractor.SearchEvent.AppLanguageChanged
import com.jpp.mpsearch.SearchInteractor.SearchEvent.NotConnectedToNetwork
import com.jpp.mpsearch.SearchInteractor.SearchEvent.UnknownError
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
class SearchInteractorTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @MockK
    private lateinit var searchRepository: SearchRepository
    @MockK
    private lateinit var languageRepository: LanguageRepository

    private val languageUpdates = MutableLiveData<SupportedLanguage>()

    private lateinit var subject: SearchInteractor

    @BeforeEach
    fun setUp() {
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        every { languageRepository.updates() } returns languageUpdates

        subject = SearchInteractor(
                connectivityRepository,
                searchRepository,
                languageRepository)
    }

    @Test
    fun `Should post NotConnectedToNetwork when a search is performed and no connectivity is detected`() {
        var posted: SearchEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.searchEvents.observeWith { posted = it }

        subject.performSearchForPage("aSearch", 1) { }

        assertEquals(NotConnectedToNetwork, posted)

        verify(exactly = 0) { searchRepository.searchPage(any(), any(), any()) }
    }

    @Test
    fun `Should post UnknownError when a search is performed and an error is detected`() {
        var posted: SearchEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { searchRepository.searchPage(any(), any(), any()) } returns null

        subject.searchEvents.observeWith { posted = it }

        subject.performSearchForPage("aSearch", 1) { }

        assertEquals(UnknownError, posted)

        verify(exactly = 1) { searchRepository.searchPage(any(), any(), any()) }
    }

    @Test
    fun `Should execute callback with search list`() {
        val searchList = listOf<SearchResult>(mockk(), mockk(), mockk())
        val searchPage = mockk<SearchPage>()
        var result: List<SearchResult>? = null
        val callback: (List<SearchResult>) -> Unit = { result = it }

        every { searchPage.results } returns searchList
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { searchRepository.searchPage(any(), any(), any()) } returns searchPage

        subject.performSearchForPage("aQuery", 1, callback)

        assertEquals(searchList, result)
        verify { searchRepository.searchPage("aQuery", 1, SupportedLanguage.English) }
    }

    @Test
    fun `Should notify when language is changed`() {
        var posted: SearchEvent? = null

        subject.searchEvents.observeWith { posted = it }

        languageUpdates.value = SupportedLanguage.Spanish

        assertEquals(AppLanguageChanged, posted)
    }
}
