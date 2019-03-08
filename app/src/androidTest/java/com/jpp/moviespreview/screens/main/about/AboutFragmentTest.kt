package com.jpp.moviespreview.screens.main.about

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jpp.moviespreview.R
import com.jpp.moviespreview.assertions.assertItemCount
import com.jpp.moviespreview.assertions.assertWithText
import com.jpp.moviespreview.assertions.withViewInRecyclerView
import com.jpp.moviespreview.di.TestMPViewModelFactory
import com.jpp.moviespreview.extras.launch
import com.jpp.moviespreview.screens.SingleLiveEvent
import com.jpp.moviespreview.testutils.FragmentTestActivity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AboutFragmentTest {

    @get:Rule
    val activityTestRule = object : ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java, true, false) {}

    private fun launchAndInjectFragment() {
        activityTestRule.activity.startFragment(AboutFragment(), this@AboutFragmentTest::inject)
    }

    private fun inject(fragment: AboutFragment) {
        every { viewModelMock.viewState() } returns viewStateLiveData
        every { viewModelMock.navEvents() } returns navigationEvents
        fragment.viewModelFactory = TestMPViewModelFactory().apply {
            addVm(viewModelMock)
        }
    }

    private val viewStateLiveData = MutableLiveData<AboutViewState>()
    private val navigationEvents = SingleLiveEvent<AboutNavEvent>()
    private val viewModelMock = mockk<AboutViewModel>(relaxed = true)
    private val supportedAboutItems by lazy {
        listOf(
                AboutItem.RateApp,
                AboutItem.ShareApp,
                AboutItem.BrowseAppCode,
                AboutItem.Licenses,
                AboutItem.TheMovieDbTermsOfUse
        )
    }

    @Before
    fun setUp() {
        activityTestRule.launch()
    }


    @Test
    fun shouldShowInitialContent() {
        val expectedAppVersion = "appVersion"

        launchFullAboutScreen()

        onAboutVersion().assertWithText(expectedAppVersion)
        onAboutRv().assertItemCount(supportedAboutItems.size)

        onView(withViewInRecyclerView(R.id.aboutRv, 0, R.id.aboutItemTitle))
                .check(matches(withText(AboutItem.RateApp.title)))

        onView(withViewInRecyclerView(R.id.aboutRv, 1, R.id.aboutItemTitle))
                .check(matches(withText(AboutItem.ShareApp.title)))

        onView(withViewInRecyclerView(R.id.aboutRv, 2, R.id.aboutItemTitle))
                .check(matches(withText(AboutItem.BrowseAppCode.title)))

        onView(withViewInRecyclerView(R.id.aboutRv, 3, R.id.aboutItemTitle))
                .check(matches(withText(AboutItem.Licenses.title)))

        onView(withViewInRecyclerView(R.id.aboutRv, 4, R.id.aboutItemTitle))
                .check(matches(withText(AboutItem.TheMovieDbTermsOfUse.title)))
    }

    @Test
    fun shouldRequestToRateApp() {
        launchFullAboutScreen()

        clickOnItemAtPosition(0)

        verify { viewModelMock.onUserSelectedAboutItem(AboutItem.RateApp) }
    }

    @Test
    fun shouldRequestToShareApp() {
        launchFullAboutScreen()

        clickOnItemAtPosition(1)

        verify { viewModelMock.onUserSelectedAboutItem(AboutItem.ShareApp) }
    }

    @Test
    fun shouldRequestToBrowseCode() {
        launchFullAboutScreen()

        clickOnItemAtPosition(2)

        verify { viewModelMock.onUserSelectedAboutItem(AboutItem.BrowseAppCode) }
    }

    @Test
    fun shouldRequestOpenLicenses() {
        launchFullAboutScreen()

        clickOnItemAtPosition(3)

        verify { viewModelMock.onUserSelectedAboutItem(AboutItem.Licenses) }
    }

    @Test
    fun shouldRequestToOpenTheMovieDbTermsOfUse() {
        launchFullAboutScreen()

        clickOnItemAtPosition(4)

        verify { viewModelMock.onUserSelectedAboutItem(AboutItem.TheMovieDbTermsOfUse) }
    }

    private fun onAboutVersion() = onView(withId(R.id.aboutVersion))
    private fun onAboutRv() = onView(withId(R.id.aboutRv))
    private fun clickOnItemAtPosition(position: Int) {
        onView(withId(R.id.aboutRv))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
    }

    private fun launchFullAboutScreen(appVersion: String = "appVersion") {
        launchAndInjectFragment()

        viewStateLiveData.postValue(AboutViewState.InitialContent(
                appVersion = appVersion,
                aboutItems = supportedAboutItems))
    }
}