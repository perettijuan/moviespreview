package com.jpp.mp.main

import android.os.Bundle
import com.jpp.mp.R
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdata.datasources.language.LanguageMonitor
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MainActivityViewModelTest {

    @RelaxedMockK
    private lateinit var languageMonitor: LanguageMonitor
    @RelaxedMockK
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: MainActivityViewModel

    @BeforeEach
    fun setUp() {
        subject = MainActivityViewModel(languageMonitor, languageRepository)
    }

    @ParameterizedTest
    @MethodSource("navigationEvents")
    fun `Should post navigation event`(destination: Destination, expectedEvent: ModuleNavigationEvent) {
        var postedEvent: ModuleNavigationEvent? = null

        subject.moduleNavEvents.observeWith { it.actionIfNotHandled { event -> postedEvent = event } }

        subject.onRequestToNavigateToDestination(destination)

        assertEquals(expectedEvent, postedEvent)
    }

    @ParameterizedTest
    @MethodSource("destinationReached")
    fun `Should post view state when destination reached`(destination: Destination, expectedViewState: MainActivityViewState) {
        var postedViewState: MainActivityViewState? = null

        subject.viewState.observeWith { viewState -> postedViewState = viewState }

        subject.onDestinationReached(destination)

        assertEquals(expectedViewState, postedViewState)
    }

    @Test
    fun `Should start monitoring in onInit`() {
        subject.onInit()

        verify { languageMonitor.startMonitoring() }
    }

    @Test
    fun `Should ask language repository to sync languages onInit`() {
        subject.onInit()

        verify { languageRepository.syncPlatformLanguage() }
    }

    companion object {
        @JvmStatic
        fun navigationEvents() = listOf(
                Arguments.arguments(
                        Destination.MPAccount,
                        ModuleNavigationEvent.NavigateToNodeWithId.toUserAccount()),
                Arguments.arguments(
                        Destination.MPMovieDetails("anId", "anUrl", "aName"),
                        ModuleNavigationEvent.NavigateToNodeWithExtras(
                                R.id.movie_details_nav,
                                Bundle().apply {
                                    putString("movieId", "anId")
                                    putString("movieImageUrl", "anUrl")
                                    putString("movieTitle", "aName")
                                })),
                Arguments.arguments(
                        Destination.MPPerson("anId", "anUrl", "aName"),
                        ModuleNavigationEvent.NavigateToNodeWithExtras(
                                R.id.person_nav,
                                Bundle().apply {
                                    putString("movieId", "anId")
                                    putString("movieImageUrl", "anUrl")
                                    putString("movieTitle", "aName")
                                })),
                Arguments.arguments(
                        Destination.MPCredits(12.toDouble(), "aName"),
                        ModuleNavigationEvent.NavigateToNodeWithExtras(
                                R.id.credits_nav,
                                Bundle().apply {
                                    putDouble("movieId", 12.toDouble())
                                    putString("movieTitle", "aName")
                                })),
                Arguments.arguments(
                        Destination.PreviousDestination,
                        ModuleNavigationEvent.NavigateToPrevious)
        )

        @JvmStatic
        fun destinationReached() = listOf(
                Arguments.arguments(
                        Destination.ReachedDestination("aTitle"),
                        MainActivityViewState(
                                sectionTitle = "aTitle",
                                menuBarEnabled = false,
                                searchEnabled = false
                        )),
                Arguments.arguments(
                        Destination.MovieListReached("aTitle"),
                        MainActivityViewState(
                                sectionTitle = "aTitle",
                                menuBarEnabled = true,
                                searchEnabled = false
                        )),
                Arguments.arguments(
                        Destination.MPSearch,
                        MainActivityViewState(
                                sectionTitle = "",
                                menuBarEnabled = false,
                                searchEnabled = true
                        )),
                Arguments.arguments(
                        Destination.MPCredits(12.toDouble(), "aTitle"),
                        MainActivityViewState(
                                sectionTitle = "aTitle",
                                menuBarEnabled = false,
                                searchEnabled = false
                        ))
        )
    }
}
