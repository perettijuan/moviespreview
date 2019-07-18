package com.jpp.mpabout

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mpdomain.AppVersion
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class AboutViewModelTest {

    @RelaxedMockK
    private lateinit var aboutInteractor: AboutInteractor

    private val lvInteractorEvents = MutableLiveData<AboutInteractor.AboutEvent>()

    private lateinit var subject: AboutViewModel

    @BeforeEach
    fun setUp() {
        every { aboutInteractor.events } returns lvInteractorEvents

        val dispatchers = object : CoroutineDispatchers {
            override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
            override fun default(): CoroutineDispatcher = Dispatchers.Unconfined
        }

        subject = AboutViewModel(dispatchers, aboutInteractor)
    }

    @Test
    fun `Should post view content when app version available`() {
        var viewStatePosted: AboutViewState? = null
        val expectedAppVersion = "v appVersion"
        val expectedAboutItems = listOf(
                AboutItem.RateApp,
                AboutItem.ShareApp,
                AboutItem.PrivacyPolicy,
                AboutItem.BrowseAppCode,
                AboutItem.Licenses,
                AboutItem.TheMovieDbTermsOfUse
        )

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(AboutInteractor.AboutEvent.AppVersionEvent(AppVersion("appVersion")))

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)

        assertEquals(View.VISIBLE, viewStatePosted?.content?.visibility)
        assertEquals(expectedAppVersion, viewStatePosted?.content?.appVersion)
        assertEquals(expectedAboutItems, viewStatePosted?.content?.aboutItems)
    }
}