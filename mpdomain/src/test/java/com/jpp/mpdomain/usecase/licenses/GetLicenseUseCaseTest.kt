package com.jpp.mpdomain.usecase.licenses

import com.jpp.mpdomain.License
import com.jpp.mpdomain.Licenses
import com.jpp.mpdomain.repository.LicensesRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetLicenseUseCaseTest {

    @RelaxedMockK
    private lateinit var licensesRepository: LicensesRepository

    private lateinit var subject: GetLicenseUseCase

    @BeforeEach
    fun setUp() {
        subject = GetLicenseUseCase.Impl(licensesRepository)
    }

    @Test
    fun `Should fail when no licenses available`() {
        every { licensesRepository.loadLicences() } returns null

        val result = subject.getLicense(6)

        assertTrue(result is GetLicenceResult.ErrorUnknown)
    }

    @Test
    fun `Should fail when license is not in the list of available licenses`() {
        every { licensesRepository.loadLicences() } returns null

        val result = subject.getLicense(10)

        assertTrue(result is GetLicenceResult.ErrorUnknown)
    }

    @Test
    fun `Should find license`() {
        every { licensesRepository.loadLicences() } returns Licenses(availableLicenses)

        val result = subject.getLicense(6)

        assertTrue(result is GetLicenceResult.Success)
        assertEquals(availableLicenses[5], (result as GetLicenceResult.Success).licence)
    }


    private val availableLicenses by lazy {
        listOf(
                License(id = 1, name = "1", url = "u1"),
                License(id = 2, name = "2", url = "u2"),
                License(id = 3, name = "3", url = "u3"),
                License(id = 4, name = "4", url = "u4"),
                License(id = 5, name = "5", url = "u5"),
                License(id = 6, name = "6", url = "u6"),
                License(id = 7, name = "7", url = "u7"),
                License(id = 8, name = "8", url = "u8")
        )
    }
}