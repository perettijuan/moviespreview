package com.jpp.mpperson

import android.view.View
import androidx.lifecycle.SavedStateHandle
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.usecase.GetPersonUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class,
    CoroutineTestExtension::class
)
class PersonViewModelTest {

    @RelaxedMockK
    private lateinit var getPersonUseCase: GetPersonUseCase

    private lateinit var subject: PersonViewModel

    @BeforeEach
    fun setUp() {
        subject =
            PersonViewModel(
                getPersonUseCase,
                CoroutineTestExtension.testDispatcher,
                SavedStateHandle()
            )
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: PersonViewState? = null

        coEvery { getPersonUseCase.execute(10.0) } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        assertNotNull(viewStatePosted)
        assertEquals("aPerson", viewStatePosted?.screenTitle)
        assertEquals("aUrl", viewStatePosted?.personImageUrl)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.birthday?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.placeOfBirth?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.deathDay?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.bio?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.dataAvailable?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should retry to fetch data when not connected and retry is executed`() {
        var viewStatePosted: PersonViewState? = null

        coEvery { getPersonUseCase.execute(10.0) } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getPersonUseCase.execute(10.0) }
        } ?: fail()
    }

    @Test
    fun `Should post error when failing to fetch user account data`() {
        var viewStatePosted: PersonViewState? = null

        coEvery { getPersonUseCase.execute(10.0) } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        assertNotNull(viewStatePosted)
        assertEquals("aPerson", viewStatePosted?.screenTitle)
        assertEquals("aUrl", viewStatePosted?.personImageUrl)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.birthday?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.placeOfBirth?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.deathDay?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.bio?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.dataAvailable?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should retry to fetch data when error unknown and retry is executed`() {
        var viewStatePosted: PersonViewState? = null

        coEvery { getPersonUseCase.execute(10.0) } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getPersonUseCase.execute(10.0) }
        } ?: fail()
    }

    @Test
    fun `Should post loading and fetch person data onInit`() {
        val states = mutableListOf<PersonViewState>()

        subject.viewState.observeWith { viewState -> states.add(viewState) }

        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        val viewStatePosted = states[0]
        assertNotNull(viewStatePosted)
        assertEquals("aPerson", viewStatePosted?.screenTitle)
        assertEquals("aUrl", viewStatePosted?.personImageUrl)
        assertEquals(View.VISIBLE, viewStatePosted?.loadingVisibility)

        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.birthday?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.placeOfBirth?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.deathDay?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.bio?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.dataAvailable?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        coVerify { getPersonUseCase.execute(10.0) }
    }

    @Test
    fun `Should post no data available when person data is empty`() {
        var viewStatePosted: PersonViewState? = null
        val person = Person(
            id = 10.0,
            name = "aName",
            biography = "",
            birthday = "",
            deathday = null,
            place_of_birth = null
        )

        coEvery { getPersonUseCase.execute(10.0) } returns Try.Success(person)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        assertNotNull(viewStatePosted)
        assertEquals("aPerson", viewStatePosted?.screenTitle)
        assertEquals("aUrl", viewStatePosted?.personImageUrl)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.birthday?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.placeOfBirth?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.deathDay?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.bio?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.dataAvailable?.visibility)
        assertEquals(
            R.string.person_no_details,
            viewStatePosted?.contentViewState?.dataAvailable?.titleRes
        )
    }

    @Test
    fun `Should post full person data`() {
        var viewStatePosted: PersonViewState? = null
        val person = Person(
            id = 10.0,
            name = "aName",
            biography = "a bio that is long",
            birthday = "aBirthday",
            deathday = "aDeathday",
            place_of_birth = "aPlaceOfBirth"
        )

        coEvery { getPersonUseCase.execute(10.0) } returns Try.Success(person)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        assertNotNull(viewStatePosted)
        assertEquals("aPerson", viewStatePosted?.screenTitle)
        assertEquals("aUrl", viewStatePosted?.personImageUrl)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.dataAvailable?.visibility)

        // birthday
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.birthday?.visibility)
        assertEquals(
            R.string.person_birthday_title,
            viewStatePosted?.contentViewState?.birthday?.titleRes
        )
        assertEquals("aBirthday", viewStatePosted?.contentViewState?.birthday?.value)

        // place of birth
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.placeOfBirth?.visibility)
        assertEquals(
            R.string.person_birth_place_title,
            viewStatePosted?.contentViewState?.placeOfBirth?.titleRes
        )
        assertEquals("aPlaceOfBirth", viewStatePosted?.contentViewState?.placeOfBirth?.value)

        // deathday
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.deathDay?.visibility)
        assertEquals(
            R.string.person_death_day_title,
            viewStatePosted?.contentViewState?.deathDay?.titleRes
        )
        assertEquals("aDeathday", viewStatePosted?.contentViewState?.deathDay?.value)

        // bio
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.bio?.visibility)
        assertEquals(R.string.person_bio_title, viewStatePosted?.contentViewState?.bio?.titleRes)
        assertEquals("a bio that is long", viewStatePosted?.contentViewState?.bio?.value)
    }
}
