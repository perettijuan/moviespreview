package com.jpp.moviespreview.screens.main.person

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jpp.moviespreview.R
import com.jpp.moviespreview.assertions.*
import com.jpp.moviespreview.di.TestMPViewModelFactory
import com.jpp.moviespreview.extras.launch
import com.jpp.moviespreview.testutils.FragmentTestActivity
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersonFragmentTest {

    @get:Rule
    val activityTestRule = object : ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java, true, false) {}

    private fun launchAndInjectFragment() {
        val fragment = PersonFragment().apply {
            arguments = Bundle().apply {
                putString("personId", "12")
                putString("personImageUrl", "anImageUrl")
                putString("personName", "aName")
            }
        }

        activityTestRule.activity.startFragment(fragment, this@PersonFragmentTest::inject)
    }

    private fun inject(fragment: PersonFragment) {
        val viewModel = mockk<PersonViewModel>(relaxed = true)
        every { viewModel.viewState() } returns viewStateLiveData
        fragment.viewModelFactory = TestMPViewModelFactory().apply {
            addVm(viewModel)
        }
    }

    private val viewStateLiveData = MutableLiveData<PersonViewState>()

    @Before
    fun setUp() {
        activityTestRule.launch()
    }

    @Test
    fun shouldShowLoading() {
        launchAndInjectFragment()

        viewStateLiveData.postValue(PersonViewState.Loading("anImageUrl", "aName"))

        onBDayRow().assertNotDisplayed()
        onPlaceOfBirthRow().assertNotDisplayed()
        onDeathDayRow().assertNotDisplayed()
        onBioTitleView().assertNotDisplayed()
        onBioBodyView().assertNotDisplayed()
        onNoInfoView().assertNotDisplayed()
        onErrorView().assertNotDisplayed()

        onLoadingView().assertDisplayed()
    }

    @Test
    fun shouldShowErrorUnknownView() {
        launchAndInjectFragment()

        viewStateLiveData.postValue(PersonViewState.ErrorUnknown)

        onBDayRow().assertNotDisplayed()
        onPlaceOfBirthRow().assertNotDisplayed()
        onDeathDayRow().assertNotDisplayed()
        onBioTitleView().assertNotDisplayed()
        onBioBodyView().assertNotDisplayed()
        onNoInfoView().assertNotDisplayed()
        onLoadingView().assertNotDisplayed()

        onErrorView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_unexpected_error_message)
    }

    @Test
    fun shouldShowConnectivityError() {
        launchAndInjectFragment()

        viewStateLiveData.postValue(PersonViewState.ErrorNoConnectivity)

        onBDayRow().assertNotDisplayed()
        onPlaceOfBirthRow().assertNotDisplayed()
        onDeathDayRow().assertNotDisplayed()
        onBioTitleView().assertNotDisplayed()
        onBioBodyView().assertNotDisplayed()
        onNoInfoView().assertNotDisplayed()
        onLoadingView().assertNotDisplayed()

        onErrorView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_no_network_connection_message)
    }

    @Test
    fun shouldShowNoData() {
        launchAndInjectFragment()

        viewStateLiveData.postValue(PersonViewState.LoadedEmpty)

        onBDayRow().assertNotDisplayed()
        onPlaceOfBirthRow().assertNotDisplayed()
        onDeathDayRow().assertNotDisplayed()
        onBioTitleView().assertNotDisplayed()
        onBioBodyView().assertNotDisplayed()
        onLoadingView().assertNotDisplayed()
        onErrorView().assertNotDisplayed()

        onNoInfoView().assertDisplayed()
    }

    @Test
    fun shouldShowFullContent() {
        val uiPerson = UiPerson(
                name = "aName",
                biography = "aBiography",
                birthday = "aBDay",
                deathday = "aDeathDay",
                placeOfBirth = "aPlaceOfBirth"
        )

        launchAndInjectFragment()

        viewStateLiveData.postValue(PersonViewState.Loaded(
                person = uiPerson,
                showBirthday = true,
                showDeathDay = true,
                showPlaceOfBirth = true
        ))

        onLoadingView().assertNotDisplayed()
        onErrorView().assertNotDisplayed()
        onNoInfoView().assertNotDisplayed()

        onBDayRow().assertDisplayed()
        onView(disambiguatingMatcher(withId(R.id.columnTextViewValue), 0))
                .check(matches(withText(uiPerson.birthday)))

        onPlaceOfBirthRow().assertDisplayed()
        onView(disambiguatingMatcher(withId(R.id.columnTextViewValue), 1))
                .assertWithText(uiPerson.placeOfBirth)

        onDeathDayRow().assertDisplayed()
        onView(disambiguatingMatcher(withId(R.id.columnTextViewValue), 2))
                .assertWithText(uiPerson.deathday)

        onBioTitleView().assertDisplayed()
        onBioBodyView().assertDisplayed()
        onBioBodyView().assertWithText(uiPerson.biography)
    }

    @Test
    fun shouldShowContentNoBDay() {
        val uiPerson = UiPerson(
                name = "aName",
                biography = "aBiography",
                birthday = "",
                deathday = "aDeathDay",
                placeOfBirth = ""
        )

        launchAndInjectFragment()

        viewStateLiveData.postValue(PersonViewState.Loaded(
                person = uiPerson,
                showBirthday = false,
                showDeathDay = true,
                showPlaceOfBirth = false
        ))

        onLoadingView().assertNotDisplayed()
        onErrorView().assertNotDisplayed()
        onNoInfoView().assertNotDisplayed()

        onBDayRow().assertNotDisplayed()
        onPlaceOfBirthRow().assertNotDisplayed()

        onDeathDayRow().assertDisplayed()
        onView(disambiguatingMatcher(withId(R.id.columnTextViewValue), 2))
                .assertWithText(uiPerson.deathday)

        onBioTitleView().assertDisplayed()
        onBioBodyView().assertDisplayed()
        onBioBodyView().assertWithText(uiPerson.biography)
    }

    @Test
    fun shouldShowContentNoDeathDay() {
        val uiPerson = UiPerson(
                name = "aName",
                biography = "aBiography",
                birthday = "aBDay",
                deathday = "",
                placeOfBirth = "aPlaceOfBirth"
        )

        launchAndInjectFragment()

        viewStateLiveData.postValue(PersonViewState.Loaded(
                person = uiPerson,
                showBirthday = true,
                showDeathDay = false,
                showPlaceOfBirth = true
        ))

        onLoadingView().assertNotDisplayed()
        onErrorView().assertNotDisplayed()
        onNoInfoView().assertNotDisplayed()

        onBDayRow().assertDisplayed()
        onView(disambiguatingMatcher(withId(R.id.columnTextViewValue), 0))
                .check(matches(withText(uiPerson.birthday)))

        onPlaceOfBirthRow().assertDisplayed()
        onView(disambiguatingMatcher(withId(R.id.columnTextViewValue), 1))
                .assertWithText(uiPerson.placeOfBirth)

        onDeathDayRow().assertNotDisplayed()
        onBioTitleView().assertDisplayed()
        onBioBodyView().assertDisplayed()
        onBioBodyView().assertWithText(uiPerson.biography)
    }

    private fun onLoadingView() = onView(withId(R.id.personLoadingView))
    private fun onErrorView() = onView(withId(R.id.personErrorView))
    private fun onBDayRow() = onView(withId(R.id.personBirthdayRow))
    private fun onPlaceOfBirthRow() = onView(withId(R.id.personPlaceOfBirthRow))
    private fun onDeathDayRow() = onView(withId(R.id.personDeathDayRow))
    private fun onBioTitleView() = onView(withId(R.id.personBioTitleTextView))
    private fun onBioBodyView() = onView(withId(R.id.personBioBodyTextView))
    private fun onNoInfoView() = onView(withId(R.id.personDetailNoInfoTextView))
}