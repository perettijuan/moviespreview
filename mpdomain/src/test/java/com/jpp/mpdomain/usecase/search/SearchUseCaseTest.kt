package com.jpp.mpdomain.usecase.search

import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.repository.SearchRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SearchUseCaseTest {

    @RelaxedMockK
    private lateinit var searchRepository: SearchRepository
    @RelaxedMockK
    private lateinit var connectivityHandler: ConnectivityHandler

    private lateinit var subject: SearchUseCase

    @BeforeEach
    fun setUp() {
        subject = SearchUseCase.Impl(searchRepository, connectivityHandler)
    }

    @Test
    fun `Should check connectivity before searching and return ErrorNoConnectivity`() {
        every { connectivityHandler.isConnectedToNetwork() } returns false

        subject.search("aSearch", 1).let { result ->
            verify(exactly = 0) { searchRepository.searchPage(any(), any()) }
            assertEquals(SearchUseCaseResult.ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        every { connectivityHandler.isConnectedToNetwork() } returns true
        every { searchRepository.searchPage(any(), any()) } returns null

        subject.search("aSearch", 1).let { result ->
            verify(exactly = 1) { searchRepository.searchPage("aSearch", 1) }
            assertEquals(SearchUseCaseResult.ErrorUnknown, result)
        }
    }

    @Test
    fun `Should filter for person and movies only`() {
        val movieResult = mockk<SearchResult>(relaxed = true)
        val personResult = mockk<SearchResult>(relaxed = true)
        val tvResult = mockk<SearchResult>(relaxed = true)
        val expectedResults = listOf(movieResult, personResult)
        val searchPage = SearchPage(
                page = 1,
                results = listOf(movieResult, personResult, tvResult),
                total_results = 10,
                total_pages = 10
        )

        every { movieResult.isMovie() } returns true
        every { personResult.isPerson() } returns true
        every { searchRepository.searchPage(any(), any()) } returns searchPage
        every { connectivityHandler.isConnectedToNetwork() } returns true

        subject.search("aSearch", 1).let { result ->
            assertTrue(result is SearchUseCaseResult.Success)
            assertEquals(expectedResults, (result as SearchUseCaseResult.Success).searchPage.results)
        }
    }
}