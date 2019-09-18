package com.jpp.mpabout

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.AppVersion
import com.jpp.mptestutils.CoroutineTestExtention
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.blockUntilCoroutinesAreDone
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(
        MockKExtension::class,
        InstantTaskExecutorExtension::class,
        CoroutineTestExtention::class
)
class AboutViewModelTest {

    @RelaxedMockK
    private lateinit var aboutInteractor: AboutInteractor

    private val lvInteractorEvents = MutableLiveData<AboutInteractor.AboutEvent>()

    private lateinit var subject: AboutViewModel

    @BeforeEach
    fun setUp() {
        every { aboutInteractor.events } returns lvInteractorEvents
        subject = AboutViewModel(aboutInteractor)
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

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        lvInteractorEvents.postValue(AboutInteractor.AboutEvent.AppVersionEvent(AppVersion("appVersion")))

        assertNotNull(viewStatePosted)
        assertEquals(R.string.about_top_bar_title, viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)

        assertEquals(View.VISIBLE, viewStatePosted?.content?.visibility)
        assertEquals(expectedAppVersion, viewStatePosted?.header?.appVersion)
        assertEquals(expectedAboutItems, viewStatePosted?.content?.aboutItems)
    }

    @ParameterizedTest
    @MethodSource("executeInteractionTests")
    fun `Should request proper URL when navigation item selected`(param: AboutInteractorTestParam) {

        subject.onUserSelectedAboutItem(param.selected)

        blockUntilCoroutinesAreDone(subject.coroutineContext)

        param.verification.invoke(aboutInteractor)
    }

    data class AboutInteractorTestParam(
            val selected: AboutItem,
            val verification: (AboutInteractor) -> Unit
    )

    companion object {

        @JvmStatic
        fun executeInteractionTests() = listOf(
                AboutInteractorTestParam(
                        selected = AboutItem.BrowseAppCode,
                        verification = { interactor ->
                            verify { interactor.getRepoUrl() }
                        }
                ),
                AboutInteractorTestParam(
                        selected = AboutItem.TheMovieDbTermsOfUse,
                        verification = { interactor ->
                            verify { interactor.getApiTermOfUseUrl() }
                        }
                ),
                AboutInteractorTestParam(
                        selected = AboutItem.PrivacyPolicy,
                        verification = { interactor ->
                            verify { interactor.getPrivacyPolicyUrl() }
                        }
                ),
                AboutInteractorTestParam(
                        selected = AboutItem.RateApp,
                        verification = { interactor ->
                            verify { interactor.getStoreUrl() }
                        }
                ),
                AboutInteractorTestParam(
                        selected = AboutItem.ShareApp,
                        verification = { interactor ->
                            verify { interactor.getShareUrl() }
                        }
                )
        )
    }
}