package com.jpp.mpdomain.usecase.licenses

import com.jpp.mpdomain.Licenses
import com.jpp.mpdomain.repository.LicensesRepository
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
class GetAppLicensesUseCaseTest {

    @RelaxedMockK
    private lateinit var licensesRepository: LicensesRepository

    private lateinit var subject: GetAppLicensesUseCase

    @BeforeEach
    fun setUp() {
        subject = GetAppLicensesUseCase.Impl(licensesRepository)
    }

    @Test
    fun `Should fetch app licenses`() {
        val licenses = mockk<Licenses>()

        every { licensesRepository.loadLicences() } returns licenses

        val actual = subject.getAppLicences()

        verify { licensesRepository.loadLicences() }
        assertTrue(actual is GetLicensesResult.Success)
        assertEquals(licenses, (actual as GetLicensesResult.Success).results)
    }


    @Test
    fun `Should fail when fetch app licenses fails`() {
        every { licensesRepository.loadLicences() } returns null

        val actual = subject.getAppLicences()

        verify { licensesRepository.loadLicences() }
        assertTrue(actual is GetLicensesResult.ErrorUnknown)
    }
}