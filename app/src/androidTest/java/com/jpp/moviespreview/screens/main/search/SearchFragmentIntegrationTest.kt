package com.jpp.moviespreview.screens.main.search


import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jpp.moviespreview.di.TestMPViewModelFactory
import com.jpp.moviespreview.screens.main.SearchViewViewModel
import com.jpp.moviespreview.testutils.FragmentTestActivity
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.repository.search.SearchListing
import com.jpp.mpdomain.repository.search.SearchRepository
import com.jpp.mpdomain.repository.search.SearchRepositoryState
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
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

    Esta muy complicado hacer este test. Se me ocurrio que, en lugar de este test, hagamos un tets
    de integracion donde injectamos la DB y la API en lugar de el Repositorio

    //TODO JPP do I need this?
    @get:Rule
    var testRule = CountingTaskExecutorRule()

    @get:Rule
    val activityTestRule = object : ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java, false, true) {
        override fun afterActivityLaunched() {
            runOnUiThread {
                activity.startFragment(SearchFragment(), this@SearchFragmentIntegrationTest::inject)
            }
        }
    }

    fun inject(searchFragment: SearchFragment) {
        val searchViewModel = SearchViewModel(mockRepository)
        val vmFactory = TestMPViewModelFactory()
        vmFactory.addVm(searchViewModel)
        vmFactory.addVm(searchViewViewModel)

        searchFragment.viewModelFactory = vmFactory
    }


    private val mockRepository = mockk<SearchRepository>()
    private val searchViewViewModel by lazy { SearchViewViewModel() }

    @Before
    fun setUp() {

    }


    @Test
    fun shouldStartASearchWhenASearchIsTriggered() {

        val slot = slot<((SearchResult) -> SearchResultItem)>()
        every { mockRepository.search("aQuery", 12, capture(slot)) } returns mockSearchingListResult(searchResults)

    }


    private fun mockSearchingListResult(itemsList: List<SearchResultItem>): PagedList<SearchResultItem> {
        val pagedList = mockk<PagedList<SearchResultItem>>()
        every { pagedList[any()] } answers { itemsList[arg(0)] }
        every { pagedList.size } returns itemsList.size
        return pagedList
    }


    private companion object {
        val searchResults by lazy {
            val list = mutableListOf<SearchResultItem>()
            for (i in 0..100) {
                list.add(SearchResultItem(i.toDouble(), "anImagePath", "Search$i", SearchResultTypeIcon.PersonType))
            }
            list
        }
    }

}