package com.jpp.mpabout

import android.view.View
import com.jpp.mpdomain.AboutUrl
import com.jpp.mpdomain.AppVersion
import com.jpp.mpdomain.repository.AboutUrlRepository
import com.jpp.mpdomain.repository.AppVersionRepository
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
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
class AboutViewModelTest {

    @MockK
    private lateinit var appVersionRepository: AppVersionRepository

    @MockK
    private lateinit var aboutUrlRepository: AboutUrlRepository

    @MockK
    private lateinit var aboutNavigator: AboutNavigator

    private lateinit var subject: AboutViewModel

    @BeforeEach
    fun setUp() {
        subject = AboutViewModel(
            appVersionRepository,
            aboutUrlRepository,
            aboutNavigator,
            CoroutineTestExtension.testDispatcher
        )
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

        coEvery { appVersionRepository.getCurrentAppVersion() } returns AppVersion("appVersion")

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)

        assertEquals(View.VISIBLE, viewStatePosted?.content?.visibility)
        assertEquals(expectedAppVersion, viewStatePosted?.header?.appVersion)
        assertEquals(expectedAboutItems, viewStatePosted?.content?.aboutItems)
    }

    @Test
    fun `Should navigate to app code`() {
        var postedEvent: AboutNavEvent? = null
        val expected = AboutNavEvent.InnerNavigation("browseAppCode")

        coEvery { aboutUrlRepository.getCodeRepoUrl() } returns AboutUrl("browseAppCode")
        subject.navEvents.observeWith { handledEvent -> postedEvent = handledEvent.peekContent() }

        subject.onUserSelectedAboutItem(AboutItem.BrowseAppCode)

        assertEquals(expected, postedEvent)
    }

    @Test
    fun `Should navigate to terms of use`() {
        var postedEvent: AboutNavEvent? = null
        val expected = AboutNavEvent.InnerNavigation("termsOfUse")

        coEvery { aboutUrlRepository.getTheMovieDbTermOfUseUrl() } returns AboutUrl("termsOfUse")
        subject.navEvents.observeWith { handledEvent -> postedEvent = handledEvent.peekContent() }

        subject.onUserSelectedAboutItem(AboutItem.TheMovieDbTermsOfUse)

        assertEquals(expected, postedEvent)
    }

    @Test
    fun `Should navigate to privacy policy`() {
        var postedEvent: AboutNavEvent? = null
        val expected = AboutNavEvent.OuterNavigation("PrivacyPolicy")

        coEvery { aboutUrlRepository.getPrivacyPolicyUrl() } returns AboutUrl("PrivacyPolicy")
        subject.navEvents.observeWith { handledEvent -> postedEvent = handledEvent.peekContent() }

        subject.onUserSelectedAboutItem(AboutItem.PrivacyPolicy)

        assertEquals(expected, postedEvent)
    }

    @Test
    fun `Should navigate to rate app`() {
        var postedEvent: AboutNavEvent? = null
        val expected = AboutNavEvent.OpenGooglePlay("RateApp")

        coEvery { aboutUrlRepository.getGPlayAppUrl() } returns AboutUrl("RateApp")
        subject.navEvents.observeWith { handledEvent -> postedEvent = handledEvent.peekContent() }

        subject.onUserSelectedAboutItem(AboutItem.RateApp)

        assertEquals(expected, postedEvent)
    }

    @Test
    fun `Should navigate to share app`() {
        var postedEvent: AboutNavEvent? = null
        val expected = AboutNavEvent.OpenSharing("ShareApp")

        coEvery { aboutUrlRepository.getSharingUrl() } returns AboutUrl("ShareApp")
        subject.navEvents.observeWith { handledEvent -> postedEvent = handledEvent.peekContent() }

        subject.onUserSelectedAboutItem(AboutItem.ShareApp)

        assertEquals(expected, postedEvent)
    }
}
