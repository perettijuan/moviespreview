package com.jpp.mpdomain.usecase.support

import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.LanguageRepository
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
class RefreshDataUseCaseTest {

    @RelaxedMockK
    private lateinit var languageRepository: LanguageRepository

    private lateinit var subject: RefreshDataUseCase

    @BeforeEach
    fun setUp() {
        subject = RefreshDataUseCase.Impl(languageRepository)
    }

    @Test
    fun `Should not update if app language is same as device language`() {
        every { languageRepository.getCurrentDeviceLanguage() } returns SupportedLanguage.English
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        val refresh = subject.shouldRefreshDataInApp()

        assertFalse(refresh)
    }

    @Test
    fun `Should update if app language is different than device language`() {
        every { languageRepository.getCurrentDeviceLanguage() } returns SupportedLanguage.Spanish
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English

        val refresh = subject.shouldRefreshDataInApp()

        assertTrue(refresh)
    }

    @Test
    fun `Should not update if app language does not exists, and also update current app language`() {
        every { languageRepository.getCurrentDeviceLanguage() } returns SupportedLanguage.English
        every { languageRepository.getCurrentAppLanguage() } returns null

        val refresh = subject.shouldRefreshDataInApp()

        assertFalse(refresh)
        verify { languageRepository.updateAppLanguage(SupportedLanguage.English) }
    }
}