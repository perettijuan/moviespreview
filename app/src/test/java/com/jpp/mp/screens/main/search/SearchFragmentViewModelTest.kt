package com.jpp.mp.screens.main.search

import androidx.lifecycle.Observer
import com.jpp.mp.utiltest.CurrentThreadExecutorService
import com.jpp.mp.InstantTaskExecutorExtension
import com.jpp.mp.resumedLifecycleOwner
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.usecase.search.ConfigSearchResultUseCase
import com.jpp.mpdomain.usecase.search.SearchUseCase
import com.jpp.mpdomain.usecase.search.SearchUseCaseResult
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class SearchFragmentViewModelTest {

    @MockK
    private lateinit var searchUseCase: SearchUseCase
    @MockK
    private lateinit var configSearchResultUseCase: ConfigSearchResultUseCase

    private lateinit var subject: SearchFragmentViewModel
    private val imageSize: Int = 12

    @BeforeEach
    fun setUp() {
        subject = SearchFragmentViewModel(
                searchUseCase = searchUseCase,
                configSearchResultUseCase = configSearchResultUseCase,
                networkExecutor = CurrentThreadExecutorService()
        )

        subject.init(imageSize)
    }

    @Test
    fun `Should post searching when search is invoked and then DonSearching with valid PagedList`() {
        val searchText = "aSearch"
        val searchViewStates = mutableListOf<SearchViewState>()
        val expectedPageResultsCount = 10

        every { searchUseCase.search(any(), 1) } returns SearchUseCaseResult.Success(createSearchPage(1, expectedPageResultsCount))
        every { configSearchResultUseCase.configure(any(), any()) } answers { arg(1) }

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            searchViewStates.add(it)
        })

        subject.search(searchText)

        assertTrue(searchViewStates[0] is SearchViewState.Searching)
        assertTrue(searchViewStates[1] is SearchViewState.DoneSearching)

        val pagedList = (searchViewStates[1] as SearchViewState.DoneSearching).pagedList
        assertEquals(expectedPageResultsCount, pagedList.size)
    }

    @Test
    fun `Should post ErrorNoConnectivity when UC detects not connected`() {
        val searchText = "aSearch"
        val searchViewStates = mutableListOf<SearchViewState>()

        every { searchUseCase.search(any(), 1) } returns SearchUseCaseResult.ErrorNoConnectivity

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            searchViewStates.add(it)
        })

        subject.search(searchText)

        assertTrue(searchViewStates[0] is SearchViewState.Searching)
        assertTrue(searchViewStates[1] is SearchViewState.ErrorNoConnectivity)
    }

    @Test
    fun `Should post ErrorUnknown when UC detects not connected`() {
        val searchText = "aSearch"
        val searchViewStates = mutableListOf<SearchViewState>()

        every { searchUseCase.search(any(), 1) } returns SearchUseCaseResult.ErrorUnknown

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            searchViewStates.add(it)
        })

        subject.search(searchText)

        assertTrue(searchViewStates[0] is SearchViewState.Searching)
        assertTrue(searchViewStates[1] is SearchViewState.ErrorUnknown)
    }

    @Test
    fun `Should post Idle when last search is cleared`() {
        val searchText = "aSearch"
        val searchViewStates = mutableListOf<SearchViewState>()


        val searchResultCapture = slot<SearchResult>()
        every { searchUseCase.search(any(), 1) } returns SearchUseCaseResult.Success(createSearchPage(1, 10))
        every { configSearchResultUseCase.configure(imageSize, capture(searchResultCapture)) } answers { searchResultCapture.captured }

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            searchViewStates.add(it)
        })

        subject.search(searchText)

        assertTrue(searchViewStates[0] is SearchViewState.Searching)
        assertTrue(searchViewStates[1] is SearchViewState.DoneSearching)

        subject.clearSearch()

        assertTrue(searchViewStates[2] is SearchViewState.Idle)
    }

    @Test
    fun `Should navigate to movies when a movie item is selected`() {
        val searchResultItem = mockk<SearchResultItem>()

        with(searchResultItem) {
            every { id } returns 22.toDouble()
            every { imagePath } returns "aPath"
            every { name } returns "aName"
            every { icon } returns mockk<SearchResultTypeIcon.MovieType>()
        }

        subject.navEvents().observe(resumedLifecycleOwner(), Observer {
            assertTrue(it is SearchViewNavigationEvent.ToMovieDetails)
            with(it as SearchViewNavigationEvent.ToMovieDetails) {
                assertEquals("22.0", movieId)
                assertEquals("aPath", movieImageUrl)
                assertEquals("aName", movieTitle)
            }
        })

        subject.onSearchItemSelected(searchResultItem)
    }


    private companion object {

        private fun createSearchPage(page: Int, totalResults: Int) = SearchPage(
                page = page,
                results = createSearchResultsForPage(page, totalResults),
                total_pages = 10,
                total_results = 200
        )


        private fun createSearchResultsForPage(page: Int, totalResults: Int = 10): List<SearchResult> {
            return mutableListOf<SearchResult>().apply {
                for (i in 1..totalResults) {
                    add(SearchResult(
                            id = (page + i).toDouble(),
                            poster_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                            backdrop_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                            profile_path = null,
                            media_type = "movie",
                            title = "aMovie",
                            name = null,
                            original_title = null,
                            original_language = null,
                            overview = null,
                            release_date = null,
                            genre_ids = null,
                            vote_count = null,
                            vote_average = null,
                            popularity = null
                    ))
                }
            }
        }

    }
}