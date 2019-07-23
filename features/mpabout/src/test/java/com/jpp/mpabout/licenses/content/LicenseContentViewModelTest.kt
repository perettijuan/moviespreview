package com.jpp.mpabout.licenses.content

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mpabout.AboutInteractor
import com.jpp.mpdomain.License
import com.jpp.mpdomain.Licenses
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class LicenseContentViewModelTest {

    @RelaxedMockK
    private lateinit var aboutInteractor: AboutInteractor

    private val lvInteractorEvents = MutableLiveData<AboutInteractor.LicensesEvent>()

    private lateinit var subject: LicenseContentViewModel

    @BeforeEach
    fun setUp() {
        every { aboutInteractor.licenseEvents } returns lvInteractorEvents

        val dispatchers = object : CoroutineDispatchers {
            override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
            override fun default(): CoroutineDispatcher = Dispatchers.Unconfined
        }

        subject = LicenseContentViewModel(dispatchers, aboutInteractor)
    }


    @Test
    fun `Should post loading and fetch licences in onInit`() {
        var viewStatePosted: LicenseContentViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onInit(5)

        assertNotNull(viewStatePosted)

        assertEquals(View.VISIBLE, viewStatePosted?.loadingVisibility)

        assertEquals(View.INVISIBLE, viewStatePosted?.content?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        verify { aboutInteractor.fetchAppLicenses() }
    }


    @Test
    fun `Should map show content with url for selected license`() {
        var viewStatePosted: LicenseContentViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onInit(5)
        lvInteractorEvents.postValue(AboutInteractor.LicensesEvent.Success(Licenses(availableLicenses)))

        assertNotNull(viewStatePosted)

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.content?.visibility)
        assertEquals("u5", viewStatePosted?.content?.licenseUrl)
    }


    @Test
    fun `Should retry to fetch licenses when error is detected and retry is taped`() {
        var viewStatePosted: LicenseContentViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(AboutInteractor.LicensesEvent.UnknownError)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify { aboutInteractor.fetchAppLicenses() }
        } ?: fail()
    }

    private companion object {
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
}