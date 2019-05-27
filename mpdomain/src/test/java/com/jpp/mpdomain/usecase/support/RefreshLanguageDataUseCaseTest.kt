package com.jpp.mpdomain.usecase.support

import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.SupportRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RefreshLanguageDataUseCaseTest {

    @RelaxedMockK
    private lateinit var languageRepository: LanguageRepository
    @RelaxedMockK
    private lateinit var supportRepository: SupportRepository

    private lateinit var subject: RefreshLanguageDataUseCase

    @BeforeEach
    fun setUp() {
        subject = RefreshLanguageDataUseCase.Impl(languageRepository, supportRepository)
    }

    @Test
    fun `Should not update if app language is same as device language`() {
        every { languageRepository.getCurrentDeviceLanguage() } returns SupportedLanguage.English
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        val refresh = subject.shouldRefreshDataInApp()

        assertFalse(refresh)
        verify(exactly = 0) { languageRepository.updateAppLanguage(any()) }
        verify(exactly = 0) { supportRepository.clearAllData() }
    }

    @Test
    fun `Should update if app language is different than device language`() {
        every { languageRepository.getCurrentDeviceLanguage() } returns SupportedLanguage.Spanish
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        val refresh = subject.shouldRefreshDataInApp()

        assertTrue(refresh)

        verify { languageRepository.updateAppLanguage(any()) }
        verify { supportRepository.clearAllData() }
    }
}