package com.jpp.mpabout.licenses.content

import android.view.View
import androidx.lifecycle.SavedStateHandle
import com.jpp.mpdomain.License
import com.jpp.mpdomain.usecase.FindAppLicenseUseCase
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
class LicenseContentViewModelTest {

    @RelaxedMockK
    private lateinit var findAppLicenseUseCase: FindAppLicenseUseCase

    private lateinit var subject: LicenseContentViewModel

    @BeforeEach
    fun setUp() {
        subject = LicenseContentViewModel(
            findAppLicenseUseCase,
            CoroutineTestExtension.testDispatcher,
            SavedStateHandle()
        )
    }

    @Test
    fun `Should map show content with url for selected license`() {
        var viewStatePosted: LicenseContentViewState? = null

        coEvery { findAppLicenseUseCase.execute(5) } returns Try.Success(
            License(
                id = 5,
                name = "5",
                url = "u5"
            )
        )
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(5)

        assertNotNull(viewStatePosted)

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.content?.visibility)
        assertEquals("u5", viewStatePosted?.content?.licenseUrl)
    }
}
