package com.jpp.mp.screens.main.search


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.jpp.mp.R
import com.jpp.mp.assertions.*
import com.jpp.mp.di.TestMPViewModelFactory
import com.jpp.mp.extras.launch
import com.jpp.mp.screens.main.SearchViewViewModel
import com.jpp.mp.testutils.FragmentTestActivity
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.usecase.search.ConfigSearchResultUseCase
import com.jpp.mpdomain.usecase.search.SearchUseCase
import com.jpp.mpdomain.usecase.search.SearchUseCaseResult
import com.jpp.mptestutils.CurrentThreadExecutorService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests the interaction between the [SearchFragment], the [SearchFragmentViewModel] and the
 * [SearchViewViewModel].
 * In order to achieve these tests, the [activityTestRule] adds a new instance of the
 * [SearchFragment] to the empty [FragmentTestActivity] and injects an instance of
 * [SearchFragmentViewModel] (with a mocked [SearchUseCase] and a mocked [ConfigSearchResultUseCase]).
 */
@RunWith(AndroidJUnit4::class)
class SearchFragmentIntegrationTest {


    @get:Rule
    val activityTestRule = object : ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java, true, false) {
        override fun afterActivityLaunched() {
            runOnUiThread {
                activity.startFragment(SearchFragment(), this@SearchFragmentIntegrationTest::inject)
            }
        }
    }

    /**
     * Injects the entire graph used by the feature, excepting
     * for the API.
     */
    fun inject(searchFragment: SearchFragment) {
        // real ViewModel
        searchViewModel = SearchFragmentViewModel(
                searchUseCase = searchUseCase,
                configSearchResultUseCase = configSearchResultUseCase,
                networkExecutor = CurrentThreadExecutorService()
        )

        // custom ViewModelFactory to inject the dependencies
        val vmFactory = TestMPViewModelFactory().apply {
            addVm(searchViewModel)
            addVm(searchViewViewModel)
        }

        searchFragment.viewModelFactory = vmFactory
    }


    private val searchUseCase = mockk<SearchUseCase>()
    private val configSearchResultUseCase = mockk<ConfigSearchResultUseCase>()

    // Hold this reference to perform a searchPage
    private val searchViewViewModel by lazy { SearchViewViewModel() }
    private lateinit var searchViewModel: SearchFragmentViewModel

    @Before
    fun setUp() {
        activityTestRule.launch()
    }

    @Test
    fun shouldStartASearchWhenASearchIsTriggered() {
        val pages = searchPages(10)

        every { searchUseCase.search(any(), any()) } answers { SearchUseCaseResult.Success(pages[arg(1)]) }
        every { configSearchResultUseCase.configure(any(), any()) } answers { arg(1) }

        searchViewViewModel.search("aQuery")

        waitForDoneSearching()

        onSearchPlaceHolderView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onLoadingSearchView().assertNotDisplayed()
        onResultsRecyclerView().assertDisplayed()
        onResultsRecyclerView().assertItemCount(20)

        /*
         * Here we verify that the SearchFragmentViewModel is properly mapping the model classes to
         * UI classes by matching each item in the recycler view with the expected value.
         */
        onView(withViewInRecyclerView(R.id.searchResultRv, 0, R.id.searchItemTitleTxt))
                .check(matches(withText(pages[1].results[0].title)))

        onView(withViewInRecyclerView(R.id.searchResultRv, 1, R.id.searchItemTitleTxt))
                .check(matches(withText(pages[1].results[1].title)))

        onView(withViewInRecyclerView(R.id.searchResultRv, 2, R.id.searchItemTitleTxt))
                .check(matches(withText(pages[1].results[2].title)))

        onView(withViewInRecyclerView(R.id.searchResultRv, 3, R.id.searchItemTitleTxt))
                .check(matches(withText(pages[1].results[3].title)))

        verify { searchUseCase.search("aQuery", 1) }
        verify { searchUseCase.search("aQuery", 2) } // check that we prefetch the second page
    }

    @Test
    fun shouldShowUnknownError() {
        every { searchUseCase.search(any(), 1) } answers { SearchUseCaseResult.ErrorUnknown }

        searchViewViewModel.search("aQuery")

        waitForViewState(SearchViewState.ErrorUnknown)

        onErrorSearchView().assertDisplayed()
        onView(withId(R.id.errorTitleTextView)).assertWithText(R.string.error_unexpected_error_message)

        onSearchPlaceHolderView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onLoadingSearchView().assertNotDisplayed()
        onResultsRecyclerView().assertNotDisplayed()
    }

    @Test
    fun shouldShowConnectivityError() {
        every { searchUseCase.search(any(), 1) } answers { SearchUseCaseResult.ErrorNoConnectivity }

        searchViewViewModel.search("aQuery")

        waitForViewState(SearchViewState.ErrorNoConnectivity)

        onErrorSearchView().assertDisplayed()
        onView(withId(R.id.errorTitleTextView)).assertWithText(R.string.error_no_network_connection_message)

        onSearchPlaceHolderView().assertNotDisplayed()
        onEmptySearchView().assertNotDisplayed()
        onLoadingSearchView().assertNotDisplayed()
        onResultsRecyclerView().assertNotDisplayed()
    }


    @Test
    fun shouldShowEmptySearchView() {
        every { searchUseCase.search(any(), any()) } answers { SearchUseCaseResult.Success(emptySearchPage()) }

        searchViewViewModel.search("aQuery")

        waitForEmptySearch()

        onEmptySearchView().assertDisplayed()

        onSearchPlaceHolderView().assertNotDisplayed()
        onErrorSearchView().assertNotDisplayed()
        onLoadingSearchView().assertNotDisplayed()
        onResultsRecyclerView().assertNotDisplayed()
    }


    private fun waitForDoneSearching() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for items in list"

            override fun checkCondition(): Boolean {
                return searchViewModel.viewState().value is SearchViewState.DoneSearching
            }
        })
    }

    private fun waitForViewState(viewState: SearchViewState) {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for items in list"

            override fun checkCondition(): Boolean {
                return searchViewModel.viewState().value == viewState
            }
        })
    }

    private fun waitForEmptySearch() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for items in list"

            override fun checkCondition(): Boolean {
                return searchViewModel.viewState().value is SearchViewState.EmptySearch
            }
        })
    }

    private fun onResultsRecyclerView() = onView(withId(R.id.searchResultRv))
    private fun onSearchPlaceHolderView() = onView(withId(R.id.searchPlaceHolderIv))
    private fun onEmptySearchView() = onView(withId(R.id.emptySearch))
    private fun onErrorSearchView() = onView(withId(R.id.searchErrorView))
    private fun onLoadingSearchView() = onView(withId(R.id.searchLoadingView))

    private companion object {

        private fun emptySearchPage() = SearchPage(
                page = 1,
                results = listOf(),
                total_pages = 1,
                total_results = 1
        )

        /**
         * Creates a list of [SearchPage].
         */
        private fun searchPages(totalPages: Int): List<SearchPage> {
            val listOfPages = mutableListOf<SearchPage>()

            for (i in 1..totalPages) {
                listOfPages.add(SearchPage(
                        page = i,
                        results = searchResults(i),
                        total_pages = totalPages,
                        total_results = totalPages * 10
                ))
            }
            return listOfPages
        }


        /**
         * Creates a list of 10 [SearchResult] using the provided [page] element
         * as suffix for each string value.
         */
        private fun searchResults(page: Int): List<SearchResult> {
            val results = mutableListOf<SearchResult>()

            for (i in 1..10) {
                results.add(SearchResult(
                        id = i.toDouble(),
                        poster_path = "posterPath$page$i",
                        profile_path = null, // for simplicity, we have all media type as movies
                        media_type = "movie",
                        name = null, // for simplicity, we have all media type as movies
                        title = "movie$page$i",
                        original_title = "movie$page$i",
                        overview = null,
                        release_date = null,
                        original_language = null,
                        backdrop_path = "backdropPath$page$i",
                        genre_ids = null,
                        vote_count = null,
                        vote_average = null,
                        popularity = null
                ))
            }

            return results
        }

    }


}