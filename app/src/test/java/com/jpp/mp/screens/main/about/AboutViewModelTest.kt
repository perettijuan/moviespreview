package com.jpp.mp.screens.main.about

import androidx.lifecycle.Observer
import com.jpp.mp.utiltest.InstantTaskExecutorExtension
import com.jpp.mp.resumedLifecycleOwner
import com.jpp.mpdomain.usecase.about.AboutNavigationType
import com.jpp.mpdomain.usecase.about.GetAboutNavigationUrlUseCase
import com.jpp.mpdomain.usecase.appversion.GetAppVersionUseCase
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class AboutViewModelTest {

    @RelaxedMockK
    private lateinit var appVersionUseCase: GetAppVersionUseCase
    @RelaxedMockK
    private lateinit var getAboutNavigationUrlUseCase: GetAboutNavigationUrlUseCase

    private lateinit var subject: AboutViewModel

    @BeforeEach
    fun setUp() {
        subject = AboutViewModel(appVersionUseCase, getAboutNavigationUrlUseCase)
    }

    @Test
    fun `Should post InitialContent on init`() {
        var postedAboutViewState: AboutViewState? = null
        val expectedAppVersion = "v appVersion"
        val expectedAboutItems = listOf(
                AboutItem.RateApp,
                AboutItem.ShareApp,
                AboutItem.PrivacyPolicy,
                AboutItem.BrowseAppCode,
                AboutItem.Licenses,
                AboutItem.TheMovieDbTermsOfUse
        )


        every { appVersionUseCase.getCurrentAppVersion() } returns "appVersion"

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            postedAboutViewState = it
        })

        subject.init()

        assertTrue(postedAboutViewState is AboutViewState.InitialContent)
        assertEquals(expectedAppVersion, (postedAboutViewState as AboutViewState.InitialContent).appVersion)
        assertEquals(expectedAboutItems, (postedAboutViewState as AboutViewState.InitialContent).aboutItems)
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `Should navigate when item selected`(param: AboutViewModelTestParam) {
        var postedEvents: AboutNavEvent? = null

        subject.navEvents().observe(resumedLifecycleOwner(), Observer {
            postedEvents = it
        })

        subject.onUserSelectedAboutItem(param.selected)

        param.verification.invoke(postedEvents, getAboutNavigationUrlUseCase)
    }


    data class AboutViewModelTestParam(
            val selected: AboutItem,
            val verification: (AboutNavEvent?, GetAboutNavigationUrlUseCase) -> Unit
    )


    companion object {

        @JvmStatic
        fun executeParameters() = listOf(
                AboutViewModelTestParam(
                        selected = AboutItem.BrowseAppCode,
                        verification = { navEvent, useCase ->
                            assertTrue(navEvent is AboutNavEvent.InnerNavigation)
                            verify { useCase.getUrlFor(AboutNavigationType.AppCodeRepo) }
                        }
                ),
                AboutViewModelTestParam(
                        selected = AboutItem.TheMovieDbTermsOfUse,
                        verification = { navEvent, useCase ->
                            assertTrue(navEvent is AboutNavEvent.InnerNavigation)
                            verify { useCase.getUrlFor(AboutNavigationType.TheMovieDbTermsOfUse) }
                        }
                ),
                AboutViewModelTestParam(
                        selected = AboutItem.PrivacyPolicy,
                        verification = { navEvent, useCase ->
                            assertTrue(navEvent is AboutNavEvent.OuterNavigation)
                            verify { useCase.getUrlFor(AboutNavigationType.PrivacyPolicy) }
                        }
                ),
                AboutViewModelTestParam(
                        selected = AboutItem.RateApp,
                        verification = { navEvent, useCase ->
                            assertTrue(navEvent is AboutNavEvent.OpenGooglePlay)
                            verify { useCase.getUrlFor(AboutNavigationType.GooglePlayApp) }
                        }
                ),
                AboutViewModelTestParam(
                        selected = AboutItem.ShareApp,
                        verification = { navEvent, useCase ->
                            assertTrue(navEvent is AboutNavEvent.OpenSharing)
                            verify { useCase.getUrlFor(AboutNavigationType.ShareApp) }
                        }
                ),
                AboutViewModelTestParam(
                        selected = AboutItem.Licenses,
                        verification = { navEvent, _ ->
                            assertTrue(navEvent is AboutNavEvent.GoToLicenses)
                        }
                )
        )
    }
}