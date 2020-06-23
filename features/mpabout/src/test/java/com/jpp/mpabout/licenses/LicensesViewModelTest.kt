package com.jpp.mpabout.licenses

import android.view.View
import com.jpp.mpdomain.License
import com.jpp.mpdomain.Licenses
import com.jpp.mpdomain.usecase.GetAppLicensesUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class,
    CoroutineTestExtension::class
)
class LicensesViewModelTest {

    @RelaxedMockK
    private lateinit var getAppLicensesUseCase: GetAppLicensesUseCase

    private lateinit var subject: LicensesViewModel

    @BeforeEach
    fun setUp() {
        subject = LicensesViewModel(getAppLicensesUseCase, CoroutineTestExtension.testDispatcher)
    }

    @Test
    fun `Should map licenses and show content when licenses are available`() {
        var viewStatePosted: LicensesViewState? = null
        val expectedLicenses = listOf(
            LicenseItem(id = 1, name = "1"),
            LicenseItem(id = 2, name = "2"),
            LicenseItem(id = 3, name = "3"),
            LicenseItem(id = 4, name = "4"),
            LicenseItem(id = 5, name = "5"),
            LicenseItem(id = 6, name = "6"),
            LicenseItem(id = 7, name = "7"),
            LicenseItem(id = 8, name = "8")
        )

        coEvery { getAppLicensesUseCase.execute() } returns Try.Success(Licenses(availableLicenses))
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.content?.visibility)
        assertEquals(expectedLicenses, viewStatePosted?.content?.licenseItems)
    }

    private companion object {
        private val availableLicenses = listOf(
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
