package com.jpp.moviespreview.screens.main.search


import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.jpp.moviespreview.R
import com.jpp.moviespreview.di.TestMPViewModelFactory
import com.jpp.moviespreview.screens.main.SearchViewViewModel
import com.jpp.moviespreview.testutils.FragmentTestActivity
import com.jpp.moviespreview.utiltest.CurrentThreadExecutorService
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.repository.configuration.ConfigurationApi
import com.jpp.mpdomain.repository.configuration.ConfigurationDb
import com.jpp.mpdomain.repository.search.SearchApi
import com.jpp.mpdomain.repository.search.SearchRepository
import com.jpp.mpdomain.repository.search.SearchRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests the interaction between the [SearchFragment], the [SearchViewModel] and the
 * [SearchViewViewModel].
 * In order to achieve these tests, the [activityTestRule] adds a new instance of the
 * [SearchFragment] to the empty [FragmentTestActivity] and injects an instance of
 * [SearchViewModel] (with a mocked [SearchRepository]) and an instance of [SearchViewViewModel].
 */
@RunWith(AndroidJUnit4::class)
class SearchFragmentIntegrationTest {


//    //TODO JPP do I need this?
//    @get:Rule
//    var testRule = CountingTaskExecutorRule()

    @get:Rule
    val activityTestRule = object : ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java) {
        override fun afterActivityLaunched() {
        }
    }

    /**
     * Injects the entire graph used by the feature, excepting
     * for the API.
     */
    fun inject(searchFragment: SearchFragment) {
        // use real repository implementation
        val repository = SearchRepositoryImpl(
                searchApi = mockSearchApi,
                configurationApi = mockConfigApi,
                configurationDb = mockConfigDb,
                connectivityHandler = mockConnectivityHandler,
                configurationHandler = mockConfigurationHandler,
                networkExecutor = CurrentThreadExecutorService()
        )

        // real ViewModel
        val searchViewModel = SearchViewModel(repository)
        // custom ViewModelFactory to inject the dependencies
        val vmFactory = TestMPViewModelFactory().apply {
            addVm(searchViewModel)
            addVm(searchViewViewModel)
        }

        searchFragment.viewModelFactory = vmFactory
    }


    private val mockSearchApi = mockk<SearchApi>(relaxed = true)
    private val mockConfigApi = mockk<ConfigurationApi>(relaxed = true)
    private val mockConfigDb = mockk<ConfigurationDb>(relaxed = true)
    private val mockConnectivityHandler = mockk<ConnectivityHandler>(relaxed = true)
    private val mockConfigurationHandler = mockk<ConfigurationHandler>(relaxed = true)

    // Hold this reference to perform a search
    private val searchViewViewModel by lazy { SearchViewViewModel() }

    @Before
    fun setUp() {
        activityTestRule.launchActivity(Intent())
    }


    @Test
    fun shouldStartASearchWhenASearchIsTriggered() {
esto tenes que ver
        activityTestRule.activity.startFragment(SearchFragment(), this@SearchFragmentIntegrationTest::inject)

        val pages = searchPages(10)

        every { mockSearchApi.performSearch(any(), any()) } answers { pages[arg(1)] }
        every { mockConnectivityHandler.isConnectedToNetwork() } returns true

        searchViewViewModel.search("aQuery")

        waitForItemsRendering()

//        Log.d("JPPLOG", "isIdle -> ${testRule.isIdle}")

        Espresso.onView(ViewMatchers.withId(R.id.searchResultRv))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }


    private fun waitForItemsRendering() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String = "Waiting for items in list"

            override fun checkCondition(): Boolean {

                val recyclerView = activityTestRule.activity.findViewById<RecyclerView>(R.id.searchResultRv)
                        ?: return false


                val adapter = recyclerView.adapter ?: return false
                return adapter.itemCount >= 20
            }
        })
    }


    private companion object {

        /**
         * Creates a list of [SearchPage].
         */
        private fun searchPages(totalPages: Int): List<SearchPage> {
            val listOfPages = mutableListOf<SearchPage>()

            for (i in 0..totalPages) {
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