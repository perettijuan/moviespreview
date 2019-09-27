package com.jpp.mp.screens.main.licenses

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jpp.mp.R
import com.jpp.mp.assertions.assertDisplayed
import com.jpp.mp.assertions.assertItemCount
import com.jpp.mp.assertions.assertNotDisplayed
import com.jpp.mp.assertions.withViewInRecyclerView
import com.jpp.mp.di.TestMPViewModelFactory
import com.jpp.mp.extras.launch
import com.jpp.mp.testutils.FragmentTestActivity
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//TODO JPP re-enable Espresso tests
@RunWith(AndroidJUnit4::class)
class LicensesFragmentTest {

//    @get:Rule
//    val activityTestRule = object : ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java, true, false) {}
//
//    private fun launchAndInjectFragment() {
//        activityTestRule.activity.startFragment(LicensesFragmentDeprecated(), this@LicensesFragmentTest::inject)
//    }
//
//    private fun inject(licensesFragment: LicensesFragmentDeprecated) {
//        every { viewModelMock.viewState() } returns viewStateLiveData
//        every { viewModelMock.navEvents() } returns navigationEvents
//        licensesFragment.viewModelFactory = TestMPViewModelFactory().apply {
//            addVm(viewModelMock)
//        }
//    }
//
//    private val viewStateLiveData = MutableLiveData<LicensesViewState>()
//    private val navigationEvents = SingleLiveEvent<LicensesNavEvent>()
//    private val viewModelMock = mockk<LicensesViewModelDeprecated>(relaxed = true)
//    private val availableLicenses by lazy {
//        listOf(
//                LicenseItem(id = 1, name = "1"),
//                LicenseItem(id = 2, name = "2"),
//                LicenseItem(id = 3, name = "3"),
//                LicenseItem(id = 4, name = "4"),
//                LicenseItem(id = 5, name = "5"),
//                LicenseItem(id = 6, name = "6"),
//                LicenseItem(id = 7, name = "7"),
//                LicenseItem(id = 8, name = "8")
//        )
//    }
//
//    @Before
//    fun setUp() {
//        activityTestRule.launch()
//    }
//
//    @Test
//    fun shouldShowLoading() {
//        launchAndInjectFragment()
//
//        viewStateLiveData.postValue(LicensesViewState.Loading)
//
//        onContentView().assertNotDisplayed()
//        onErrorView().assertNotDisplayed()
//
//        onLoadingView().assertDisplayed()
//    }
//
//    @Test
//    fun shouldShowLicenseList() {
//        launchAndInjectFragment()
//
//        viewStateLiveData.postValue(LicensesViewState.Loaded(availableLicenses))
//
//        onErrorView().assertNotDisplayed()
//        onLoadingView().assertNotDisplayed()
//
//        onContentView().assertDisplayed()
//        onContentView().assertItemCount(availableLicenses.size)
//
//        onView(withViewInRecyclerView(R.id.licensesRv, 0, android.R.id.text1))
//                .check(matches(withText(availableLicenses[0].name)))
//
//        onView(withViewInRecyclerView(R.id.licensesRv, 1, android.R.id.text1))
//                .check(matches(withText(availableLicenses[1].name)))
//
//        onView(withViewInRecyclerView(R.id.licensesRv, 2, android.R.id.text1))
//                .check(matches(withText(availableLicenses[2].name)))
//
//        onView(withViewInRecyclerView(R.id.licensesRv, 3, android.R.id.text1))
//                .check(matches(withText(availableLicenses[3].name)))
//
//        onView(withViewInRecyclerView(R.id.licensesRv, 4, android.R.id.text1))
//                .check(matches(withText(availableLicenses[4].name)))
//    }
//
//    @Test
//    fun shouldShowError() {
//        launchAndInjectFragment()
//
//        viewStateLiveData.postValue(LicensesViewState.ErrorUnknown)
//
//        onContentView().assertNotDisplayed()
//        onLoadingView().assertNotDisplayed()
//
//        onErrorView().assertDisplayed()
//    }
//
//    private fun onLoadingView() = onView(withId(R.id.licensesLoadingView))
//    private fun onErrorView() = onView(withId(R.id.licensesErrorView))
//    private fun onContentView() = onView(withId(R.id.licensesRv))
}