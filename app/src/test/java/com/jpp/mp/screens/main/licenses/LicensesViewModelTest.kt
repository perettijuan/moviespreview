package com.jpp.mp.screens.main.licenses

import com.jpp.mp.screens.main.TestCoroutineDispatchers
import com.jpp.mpdomain.License
import com.jpp.mpdomain.Licenses
import com.jpp.mpdomain.usecase.licenses.GetAppLicensesUseCase
import com.jpp.mpdomain.usecase.licenses.GetLicensesResult
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class LicensesViewModelTest {

    @MockK
    private lateinit var getAppLicensesUseCase: GetAppLicensesUseCase

    private lateinit var subject: LicensesViewModel

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

    @BeforeEach
    fun setUp() {
        subject = LicensesViewModel(TestCoroutineDispatchers(), getAppLicensesUseCase)
    }

    @Test
    fun `Should execute GetAppLicensesUseCase, adapt result to UI model and post value on init`() {
        val viewStatePosted = mutableListOf<LicensesViewState>()

        val expectedLicenses by lazy {
            listOf(
                    LicenseItem(id = 1, name = "1"),
                    LicenseItem(id = 2, name = "2"),
                    LicenseItem(id = 3, name = "3"),
                    LicenseItem(id = 4, name = "4"),
                    LicenseItem(id = 5, name = "5"),
                    LicenseItem(id = 6, name = "6"),
                    LicenseItem(id = 7, name = "7"),
                    LicenseItem(id = 8, name = "8")
            )
        }

        every { getAppLicensesUseCase.getAppLicences() } returns GetLicensesResult.Success(Licenses(availableLicenses))

        subject.viewState().observeWith { viewStatePosted.add(it) }

        subject.init()

        assertTrue(viewStatePosted[0] is LicensesViewState.Loading)
        assertTrue(viewStatePosted[1] is LicensesViewState.Loaded)
        assertEquals(expectedLicenses, (viewStatePosted[1] as LicensesViewState.Loaded).licenses)
        verify(exactly = 1) { getAppLicensesUseCase.getAppLicences() }
    }

    @Test
    fun `Should fetch licenses and show unknown error`() {
        val viewStatePosted = mutableListOf<LicensesViewState>()

        every { getAppLicensesUseCase.getAppLicences() } returns GetLicensesResult.ErrorUnknown

        subject.viewState().observeWith { viewStatePosted.add(it) }

        subject.init()

        assertTrue(viewStatePosted[0] is LicensesViewState.Loading)
        assertTrue(viewStatePosted[1] is LicensesViewState.ErrorUnknown)
    }


    @Test
    fun `Should retry if state is error unknown`() {
        every { getAppLicensesUseCase.getAppLicences() } returns GetLicensesResult.ErrorUnknown

        subject.init()
        subject.retry()

        verify(exactly = 2) { getAppLicensesUseCase.getAppLicences() }
    }


    @Test
    fun `Should not retry if state is success`() {
        every { getAppLicensesUseCase.getAppLicences() } returns GetLicensesResult.Success(Licenses(availableLicenses))

        subject.init()
        subject.retry()

        verify(exactly = 1) { getAppLicensesUseCase.getAppLicences() }
    }

    @Test
    fun `Should navigate to license content when a license is selected`() {
        val reqLicenceName = "aLicenseName"
        val reqLicenseId = 12


        subject.navEvents().observeWith {
            assertTrue(it is LicensesNavEvent.ToLicenseContent)
            with(it as LicensesNavEvent.ToLicenseContent) {
                assertEquals(reqLicenceName, licenseName)
                assertEquals(reqLicenseId, licenseId)
            }
        }

        subject.onUserSelectedLicense(LicenseItem(id = reqLicenseId, name = reqLicenceName))
    }

}