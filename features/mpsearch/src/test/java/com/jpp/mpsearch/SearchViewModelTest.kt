package com.jpp.mpsearch

import androidx.lifecycle.MutableLiveData
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

        subject = SearchViewModel(
                TestSearchCoroutineDispatchers(),
                searchInteractor,
                imagesPathInteractor
        )
    }

    @Test
    fun `Should post ShowSearchView on init`() {
        var viewStatePosted: SearchViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onInit(10)

        assertEquals(SearchViewState.ShowSearchView, viewStatePosted)
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: SearchViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(NotConnectedToNetwork)

        assertEquals(SearchViewState.ShowNotConnected, viewStatePosted)
    }

    @Test
    fun `Should post error when failing to fetch user account data`() {
        var viewStatePosted: SearchViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(UnknownError)

        assertEquals(SearchViewState.ShowError, viewStatePosted)
    }

    @Test
    fun `Should post loading and create paged list with first movie pages onInitWithFavorites`() {
        val viewStatesPosted = mutableListOf<SearchViewState>()


        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatesPosted.add(viewState) } }

        subject.onSearch("aQuery")

        assertEquals(SearchViewState.ShowSearching, viewStatesPosted[0])
        assertTrue(viewStatesPosted[1] is SearchViewState.ShowSearchResults)
        verify { searchInteractor.performSearchForPage("aQuery", 1, any()) }
    }
}