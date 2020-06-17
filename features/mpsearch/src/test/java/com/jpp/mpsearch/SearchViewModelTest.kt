package com.jpp.mpsearch

import android.view.View
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.usecase.SearchUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class,
    CoroutineTestExtension::class
)
class SearchViewModelTest {

    @MockK
    private lateinit var searchUseCase: SearchUseCase

    private lateinit var subject: SearchViewModel

    @BeforeEach
    fun setUp() {
        subject = SearchViewModel(
            searchUseCase,
            CoroutineTestExtension.testDispatcher
        )
    }

    @Test
    fun `Should post clear state on init`() {
        var viewStatePosted: SearchViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.searchResultsVisibility)
        assertEquals(
            View.INVISIBLE,
            viewStatePosted?.contentViewState?.emptySearchResultsVisibility
        )
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.placeHolderViewState?.visibility)
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: SearchViewState? = null

        coEvery {
            searchUseCase.execute(
                any(),
                any()
            )
        } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()
        subject.onSearch("aSearch")

        assertNotNull(viewStatePosted)

        assertEquals("aSearch", viewStatePosted?.searchQuery)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.searchResultsVisibility)
        assertEquals(
            View.INVISIBLE,
            viewStatePosted?.contentViewState?.emptySearchResultsVisibility
        )
        assertEquals(View.INVISIBLE, viewStatePosted?.placeHolderViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should post error when failing to fetch user account data`() {
        var viewStatePosted: SearchViewState? = null

        coEvery {
            searchUseCase.execute(
                any(),
                any()
            )
        } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()
        subject.onSearch("aSearch")

        assertNotNull(viewStatePosted)

        assertEquals("aSearch", viewStatePosted?.searchQuery)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.searchResultsVisibility)
        assertEquals(
            View.INVISIBLE,
            viewStatePosted?.contentViewState?.emptySearchResultsVisibility
        )
        assertEquals(View.INVISIBLE, viewStatePosted?.placeHolderViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }


    @Test
    fun `Should update reached destination in onInit`() {
        var destinationReached: Destination? = null
        val expected = Destination.MPSearch

        subject.destinationEvents.observeWith { destinationReached = it }

        subject.onInit()

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
            personName = "aName"
        )

        var requestedDestination: Destination? = null

        subject.navigationEvents.observeWith {
            it.actionIfNotHandled { dest ->
                requestedDestination = dest
            }
        }
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
            movieTitle = "aName"
        )

        var requestedDestination: Destination? = null

        subject.navigationEvents.observeWith {
            it.actionIfNotHandled { dest ->
                requestedDestination = dest
            }
        }
        subject.onItemSelected(personItem)

        assertEquals(expectedDestination, requestedDestination)
    }

    @Test
    fun `Should post empty result`() {
        var viewStatePosted: SearchViewState? = null

        val searchPage = SearchPage(
            page = 1,
            total_results = 100,
            total_pages = 10,
            results = listOf()
        )

        coEvery { searchUseCase.execute("aSearch", 1) } returns Try.Success(searchPage)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()
        subject.onSearch("aSearch")

        assertNotNull(viewStatePosted)

        assertEquals("aSearch", viewStatePosted?.searchQuery)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(
            View.VISIBLE,
            viewStatePosted?.contentViewState?.emptySearchResultsVisibility
        )
        assertEquals(View.INVISIBLE, viewStatePosted?.placeHolderViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.searchResultsVisibility)
    }

    @Test
    fun `Should perform search and map results`() {
        var viewStatePosted: SearchViewState? = null

        val searchPage = SearchPage(
            page = 1,
            total_results = 100,
            total_pages = 10,
            results = SEARCH_RESULTS
        )

        coEvery { searchUseCase.execute("aSearch", 1) } returns Try.Success(searchPage)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()
        subject.onSearch("aSearch")

        assertNotNull(viewStatePosted)

        assertEquals("aSearch", viewStatePosted?.searchQuery)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(
            View.INVISIBLE,
            viewStatePosted?.contentViewState?.emptySearchResultsVisibility
        )
        assertEquals(View.INVISIBLE, viewStatePosted?.placeHolderViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.searchResultsVisibility)
        assertEquals(SEARCH_RESULTS.size, viewStatePosted?.contentViewState?.searchResultList?.size)

        assertEquals(12.0, viewStatePosted?.contentViewState?.searchResultList?.get(0)?.id)
        assertEquals(
            "/12.jpg",
            viewStatePosted?.contentViewState?.searchResultList?.get(0)?.imagePath
        )
        assertEquals("aPerson12", viewStatePosted?.contentViewState?.searchResultList?.get(0)?.name)
        assertEquals(
            SearchResultTypeIcon.Person,
            viewStatePosted?.contentViewState?.searchResultList?.get(0)?.icon
        )

        assertEquals(14.0, viewStatePosted?.contentViewState?.searchResultList?.get(1)?.id)
        assertEquals(
            "/14P.jpg",
            viewStatePosted?.contentViewState?.searchResultList?.get(1)?.imagePath
        )
        assertEquals("aMovie14", viewStatePosted?.contentViewState?.searchResultList?.get(1)?.name)
        assertEquals(
            SearchResultTypeIcon.Movie,
            viewStatePosted?.contentViewState?.searchResultList?.get(1)?.icon
        )

        assertEquals(15.0, viewStatePosted?.contentViewState?.searchResultList?.get(2)?.id)
        assertEquals(
            "/15P.jpg",
            viewStatePosted?.contentViewState?.searchResultList?.get(2)?.imagePath
        )
        assertEquals("aMovie15", viewStatePosted?.contentViewState?.searchResultList?.get(2)?.name)
        assertEquals(
            SearchResultTypeIcon.Movie,
            viewStatePosted?.contentViewState?.searchResultList?.get(2)?.icon
        )

        assertEquals(13.0, viewStatePosted?.contentViewState?.searchResultList?.get(3)?.id)
        assertEquals(
            "/13.jpg",
            viewStatePosted?.contentViewState?.searchResultList?.get(3)?.imagePath
        )
        assertEquals("aPerson13", viewStatePosted?.contentViewState?.searchResultList?.get(3)?.name)
        assertEquals(
            SearchResultTypeIcon.Person,
            viewStatePosted?.contentViewState?.searchResultList?.get(3)?.icon
        )
    }

    private companion object {
        val SEARCH_RESULTS = listOf(
            SearchResult(
                id = 12.0,
                poster_path = null,
                backdrop_path = null,
                profile_path = "/12.jpg",
                media_type = "person",
                title = null,
                name = "aPerson12",
                original_title = null,
                original_language = null,
                overview = null,
                release_date = null,
                genre_ids = null,
                vote_count = null,
                vote_average = null,
                popularity = null
            ),
            SearchResult(
                id = 14.0,
                poster_path = "/14P.jpg",
                backdrop_path = "/14B.jpg",
                profile_path = null,
                media_type = "movie",
                title = "aMovie14",
                name = null,
                original_title = null,
                original_language = null,
                overview = null,
                release_date = null,
                genre_ids = null,
                vote_count = null,
                vote_average = null,
                popularity = null
            ),
            SearchResult(
                id = 15.0,
                poster_path = "/15P.jpg",
                backdrop_path = "/15B.jpg",
                profile_path = null,
                media_type = "movie",
                title = "aMovie15",
                name = null,
                original_title = null,
                original_language = null,
                overview = null,
                release_date = null,
                genre_ids = null,
                vote_count = null,
                vote_average = null,
                popularity = null
            ),
            SearchResult(
                id = 13.0,
                poster_path = null,
                backdrop_path = null,
                profile_path = "/13.jpg",
                media_type = "person",
                title = null,
                name = "aPerson13",
                original_title = null,
                original_language = null,
                overview = null,
                release_date = null,
                genre_ids = null,
                vote_count = null,
                vote_average = null,
                popularity = null
            )
        )
    }
}
