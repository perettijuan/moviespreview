package com.jpp.mp.screens.main.about

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

//TODO JPP re-enable espresso tests
@RunWith(AndroidJUnit4::class)
class AboutFragmentTest {
//
//    @get:Rule
//    val activityTestRule = object : ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java, true, false) {}
//
//    private fun launchAndInjectFragment() {
//        activityTestRule.activity.startFragment(AboutFragmentDeprecated(), this@AboutFragmentTest::inject)
//    }
//
//    private fun inject(fragment: AboutFragmentDeprecated) {
//        every { viewModelMock.viewState() } returns viewStateLiveData
//        every { viewModelMock.navEvents() } returns navigationEvents
//        fragment.viewModelFactory = TestMPViewModelFactory().apply {
//            addVm(viewModelMock)
//        }
//    }
//
//    private val viewStateLiveData = MutableLiveData<AboutViewState>()
//    private val navigationEvents = SingleLiveEvent<AboutNavEvent>()
//    private val viewModelMock = mockk<AboutViewModelDeprecated>(relaxed = true)
//    private val supportedAboutItems by lazy {
//        listOf(
//                AboutItem.RateApp,
//                AboutItem.ShareApp,
//                AboutItem.BrowseAppCode,
//                AboutItem.Licenses,
//                AboutItem.TheMovieDbTermsOfUse
//        )
//    }
//
//    @Before
//    fun setUp() {
//        activityTestRule.launch()
//    }
//
//
//    @Test
//    fun shouldShowInitialContent() {
//        val expectedAppVersion = "appVersion"
//
//        launchFullAboutScreen()
//
//        onAboutVersion().assertWithText(expectedAppVersion)
//        onAboutRv().assertItemCount(supportedAboutItems.size)
//
//        onView(withViewInRecyclerView(R.id.aboutRv, 0, R.id.aboutItemTitle))
//                .check(matches(withText(AboutItem.RateApp.title)))
//
//        onView(withViewInRecyclerView(R.id.aboutRv, 1, R.id.aboutItemTitle))
//                .check(matches(withText(AboutItem.ShareApp.title)))
//
//        onView(withViewInRecyclerView(R.id.aboutRv, 2, R.id.aboutItemTitle))
//                .check(matches(withText(AboutItem.BrowseAppCode.title)))
//
//        onView(withViewInRecyclerView(R.id.aboutRv, 3, R.id.aboutItemTitle))
//                .check(matches(withText(AboutItem.Licenses.title)))
//
//        onView(withViewInRecyclerView(R.id.aboutRv, 4, R.id.aboutItemTitle))
//                .check(matches(withText(AboutItem.TheMovieDbTermsOfUse.title)))
//    }
//
//    @Test
//    fun shouldRequestToRateApp() {
//        launchFullAboutScreen()
//
//        clickOnItemAtPosition(0)
//
//        verify { viewModelMock.onUserSelectedAboutItem(AboutItem.RateApp) }
//    }
//
//    @Test
//    fun shouldRequestToShareApp() {
//        launchFullAboutScreen()
//
//        clickOnItemAtPosition(1)
//
//        verify { viewModelMock.onUserSelectedAboutItem(AboutItem.ShareApp) }
//    }
//
//    @Test
//    fun shouldRequestToBrowseCode() {
//        launchFullAboutScreen()
//
//        clickOnItemAtPosition(2)
//
//        verify { viewModelMock.onUserSelectedAboutItem(AboutItem.BrowseAppCode) }
//    }
//
//    @Test
//    fun shouldRequestOpenLicenses() {
//        launchFullAboutScreen()
//
//        clickOnItemAtPosition(3)
//
//        verify { viewModelMock.onUserSelectedAboutItem(AboutItem.Licenses) }
//    }
//
//    @Test
//    fun shouldRequestToOpenTheMovieDbTermsOfUse() {
//        launchFullAboutScreen()
//
//        clickOnItemAtPosition(4)
//
//        verify { viewModelMock.onUserSelectedAboutItem(AboutItem.TheMovieDbTermsOfUse) }
//    }
//
//    private fun onAboutVersion() = onView(withId(R.id.aboutVersion))
//    private fun onAboutRv() = onView(withId(R.id.aboutRv))
//    private fun clickOnItemAtPosition(position: Int) {
//        onView(withId(R.id.aboutRv))
//                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
//    }
//
//    private fun launchFullAboutScreen(appVersion: String = "appVersion") {
//        launchAndInjectFragment()
//
//        viewStateLiveData.postValue(AboutViewState.InitialContent(
//                appVersion = appVersion,
//                aboutItems = supportedAboutItems))
//    }
}