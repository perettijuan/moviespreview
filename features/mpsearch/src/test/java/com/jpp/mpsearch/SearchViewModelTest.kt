package com.jpp.mpsearch

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mpsearch.SearchInteractor.SearchEvent
import com.jpp.mpsearch.SearchInteractor.SearchEvent.NotConnectedToNetwork
import com.jpp.mpsearch.SearchInteractor.SearchEvent.UnknownError
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class SearchViewModelTest {

    @RelaxedMockK
    private lateinit var searchInteractor: SearchInteractor
    @MockK
    private lateinit var imagesPathInteractor: ImagesPathInteractor

    private val lvInteractorEvents = MutableLiveData<SearchEvent>()

    private lateinit var subject: SearchViewModel

    @BeforeEach
    fun setUp() {
        every { searchInteractor.searchEvents } returns lvInteractorEvents

        val dispatchers = object : CoroutineDispatchers {
            override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
            override fun default(): CoroutineDispatcher = Dispatchers.Unconfined
        }

        subject = SearchViewModel(
                dispatchers,
                searchInteractor,
                imagesPathInteractor
        )

        /*
         * Since the ViewModel uses a MediatorLiveData, we need to have
         * an observer on the view states attached all the time in order
         * to get notifications.
         */
        subject.viewState.observeForever { }
    }

    @Test
    fun `Should post clear state on init`() {
        var viewStatePosted: SearchViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(10)

        assertNotNull(viewStatePosted)

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.searchResultsVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.emptySearchResultsVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.placeHolderViewState?.visibility)
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: SearchViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onSearch("aSearch")
        lvInteractorEvents.postValue(NotConnectedToNetwork)

        assertNotNull(viewStatePosted)

        assertEquals("aSearch", viewStatePosted?.searchQuery)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.searchResultsVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.emptySearchResultsVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.placeHolderViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should post error when failing to fetch user account data`() {
        var viewStatePosted: SearchViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onSearch("aSearch")
        lvInteractorEvents.postValue(UnknownError)

        assertNotNull(viewStatePosted)

        assertEquals("aSearch", viewStatePosted?.searchQuery)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.searchResultsVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.emptySearchResultsVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.placeHolderViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should refresh data when no search has been performed and language changes`() {
        subject.onSearch("aSearch")
        lvInteractorEvents.value = SearchEvent.AppLanguageChanged
        verify { searchInteractor.flushCurrentSearch() }
    }

    @Test
    fun `Should update reached destination in onInit`() {
        var destinationReached: Destination? = null
        val expected = Destination.MPSearch

        subject.destinationEvents.observeWith { destinationReached = it }

        subject.onInit(10)

        assertEquals(expected, destinationReached)
    }

    @Test
    fun `Should request navigation to person details when onItemSelected with person item`() {
        val personItem = SearchResultItem(
                id = 10.0,
                imagePath = "aPath",
                name = "aName",
                icon = SearchResultTypeIcon.Person
        )

        val expectedDestination = Destination.MPPerson(
                personId = "10.0",
                personImageUrl = "aPath",
                personName = "aName")

        var requestedDestination: Destination? = null

        subject.navigationEvents.observeWith { it.actionIfNotHandled { dest -> requestedDestination = dest } }
        subject.onItemSelected(personItem)

        assertEquals(expectedDestination, requestedDestination)
    }

    @Test
    fun `Should request navigation to movie details when onItemSelected with movie item`() {
        val personItem = SearchResultItem(
                id = 10.0,
                imagePath = "aPath",
                name = "aName",
                icon = SearchResultTypeIcon.Movie
        )

        val expectedDestination = Destination.MPMovieDetails(
                movieId = "10.0",
                movieImageUrl = "aPath",
                movieTitle = "aName")

        var requestedDestination: Destination? = null

        subject.navigationEvents.observeWith { it.actionIfNotHandled { dest -> requestedDestination = dest } }
        subject.onItemSelected(personItem)

        assertEquals(expectedDestination, requestedDestination)
    }
}