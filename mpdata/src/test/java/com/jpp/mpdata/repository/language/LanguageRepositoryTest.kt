package com.jpp.mpdata.repository.language

import com.jpp.mpdata.datasources.language.LanguageDb
import com.jpp.mpdata.datasources.language.LanguageMonitor
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class LanguageRepositoryTest {

    @RelaxedMockK
    private lateinit var languageDb: LanguageDb

    @RelaxedMockK
    private lateinit var languageMonitor: LanguageMonitor

    @RelaxedMockK
    private lateinit var localeWrapper: LocaleWrapper

    private val lambdaSlot = slot<() -> Unit>()

    private lateinit var subject: LanguageRepository

    @BeforeEach
    fun setUp() {
        subject = LanguageRepositoryImpl(languageDb, languageMonitor, localeWrapper)
        verify { languageMonitor.addListener(capture(lambdaSlot)) }
    }

    @Test
    fun `Should add new listener to language monitor on object creation`() {
        verify { languageMonitor.addListener(any()) }
    }

    @Test
    fun `Should store new default locale and notify when Locale changes`() {
        val default = mockk<Locale>()
        var posted: SupportedLanguage? = null

        every { default.language } returns "es"
        every { localeWrapper.getDefault() } returns default
        every { localeWrapper.localeFrom("es") } returns default

        subject.updates().observeWith { posted = it }

        lambdaSlot.captured.invoke()

        verify { languageDb.updateLanguageString("es") }
        assertEquals(SupportedLanguage.Spanish, posted)
    }

    @Test
    fun `Should return language from DB if exists`() = runBlocking {
        val localeEn = mockk<Locale>()

        every { languageDb.getStoredLanguageString() } returns "en"
        every { localeEn.language } returns "en"
        every { localeWrapper.localeFrom("en") } returns localeEn

        val retrieved = subject.getCurrentAppLanguage()

        assertEquals(SupportedLanguage.English, retrieved)
    }

    @Test
    fun `Should update locale in DB and return it when no one is stored`() = runBlocking {
        val localeEn = mockk<Locale>()

        every { localeWrapper.getDefault() } returns Locale.ENGLISH
        every { languageDb.getStoredLanguageString() } returns null
        every { localeEn.language } returns "en"
        every { localeWrapper.localeFrom("en") } returns localeEn

        val retrieved = subject.getCurrentAppLanguage()

        assertEquals(SupportedLanguage.English, retrieved)
        verify { languageDb.updateLanguageString("en") }
    }
}
