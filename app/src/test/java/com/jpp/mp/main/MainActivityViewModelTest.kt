package com.jpp.mp.main

import com.jpp.mpdata.datasources.language.LanguageMonitor
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.SupportRepository
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class,
    CoroutineTestExtension::class
)
class MainActivityViewModelTest {

    @RelaxedMockK
    private lateinit var languageMonitor: LanguageMonitor

    @RelaxedMockK
    private lateinit var languageRepository: LanguageRepository

    @RelaxedMockK
    private lateinit var supportRepository: SupportRepository

    @RelaxedMockK
    private lateinit var localeWrapper: LocaleWrapper

    private lateinit var subject: MainActivityViewModel

    @BeforeEach
    fun setUp() {
        subject = MainActivityViewModel(
            languageMonitor,
            languageRepository,
            supportRepository,
            localeWrapper,
            CoroutineTestExtension.testDispatcher
        )
    }

    @Test
    fun `Should sync app language in onInit`() {
        val default = mockk<Locale>()

        every { default.language } returns "es"
        every { localeWrapper.getDefault() } returns default
        every { localeWrapper.localeFrom("es") } returns default
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        subject.onInit()

        coVerify { languageRepository.updateCurrentLanguage(SupportedLanguage.Spanish) }
        coVerify { supportRepository.clearAllData() }
    }

    @Test
    fun `Should sync app language when updated platform language`() {
        val lambdaSlot = slot<() -> Unit>()
        subject.onInit()
        verify { languageMonitor.addListener(capture(lambdaSlot)) }

        val default = mockk<Locale>()

        every { default.language } returns "es"
        every { localeWrapper.getDefault() } returns default
        every { localeWrapper.localeFrom("es") } returns default
        coEvery { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        lambdaSlot.captured.invoke()

        coVerify { languageRepository.updateCurrentLanguage(SupportedLanguage.Spanish) }
        coVerify { supportRepository.clearAllData() }
    }

    @Test
    fun `Should start monitoring in onInit`() {
        subject.onInit()
        verify { languageMonitor.addListener(any()) }
    }
}
