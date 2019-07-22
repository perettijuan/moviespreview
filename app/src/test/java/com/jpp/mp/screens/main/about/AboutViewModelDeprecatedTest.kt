package com.jpp.mp.screens.main.about

import com.jpp.mpdomain.usecase.about.AboutNavigationType
import com.jpp.mpdomain.usecase.about.GetAboutNavigationUrlUseCase
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class AboutViewModelDeprecatedTest {


    @RelaxedMockK
    private lateinit var getAboutNavigationUrlUseCase: GetAboutNavigationUrlUseCase

    private lateinit var subject: AboutViewModelDeprecated

    @BeforeEach
    fun setUp() {
        subject = AboutViewModelDeprecated(getAboutNavigationUrlUseCase)
    }


    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `Should navigate when item selected`(param: AboutViewModelTestParam) {
        var postedEvents: AboutNavEvent? = null

        subject.navEvents().observeWith { postedEvents = it }

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