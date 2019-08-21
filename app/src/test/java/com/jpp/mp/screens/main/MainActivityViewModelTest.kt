package com.jpp.mp.screens.main

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
    fun `Should post navigation event`(param: NavigationEventsParams) {
        subject.viewState().observeWith { assertEquals(param.expected, it.peekContent()) }
        param.action.invoke(subject)
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


    data class NavigationEventsParams(
            val expected: MainActivityViewState,
            val action: (MainActivityViewModel) -> Unit
    )

    companion object {
        @JvmStatic
        fun navigationEvents() = listOf(
                NavigationEventsParams(
                        MainActivityViewState(sectionTitle = "", menuBarEnabled = false, searchEnabled = true),
                        action = { it.userNavigatesToSearch() }
                )
        )
    }
}