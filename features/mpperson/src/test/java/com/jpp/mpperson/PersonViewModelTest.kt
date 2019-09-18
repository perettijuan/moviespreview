package com.jpp.mpperson

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.Person
import com.jpp.mptestutils.CoroutineTestExtention
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
        MockKExtension::class,
        InstantTaskExecutorExtension::class,
        CoroutineTestExtention::class
)
class PersonViewModelTest {

    @RelaxedMockK
    private lateinit var interactor: PersonInteractor

    private val lvInteractorEvents = MutableLiveData<PersonInteractor.PersonEvent>()

    private lateinit var subject: PersonViewModel

    @BeforeEach
    fun setUp() {
        every { interactor.events } returns lvInteractorEvents

        subject = PersonViewModel(interactor)
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: PersonViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        lvInteractorEvents.postValue(PersonInteractor.PersonEvent.NotConnectedToNetwork)

        assertNotNull(viewStatePosted)
        assertEquals("aPerson", viewStatePosted?.screenTitle)
        assertEquals("emptyUrl", viewStatePosted?.personImageUrl)
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

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))
        lvInteractorEvents.postValue(PersonInteractor.PersonEvent.NotConnectedToNetwork)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify(exactly = 2) { interactor.fetchPerson(10.0) }
        } ?: fail()
    }

    @Test
    fun `Should post error when failing to fetch user account data`() {
        var viewStatePosted: PersonViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        lvInteractorEvents.postValue(PersonInteractor.PersonEvent.UnknownError)

        assertNotNull(viewStatePosted)
        assertEquals("aPerson", viewStatePosted?.screenTitle)
        assertEquals("emptyUrl", viewStatePosted?.personImageUrl)
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

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))
        lvInteractorEvents.postValue(PersonInteractor.PersonEvent.UnknownError)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify(exactly = 2) { interactor.fetchPerson(10.0) }
        } ?: fail()
    }


    @Test
    fun `Should post loading and fetch person data onInit`() {
        var viewStatePosted: PersonViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

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

        verify { interactor.fetchPerson(10.0) }
    }

    @Test
    fun `Should post loading, clear data and fetch person data on language changed `() {
        var viewStatePosted: PersonViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))
        lvInteractorEvents.postValue(PersonInteractor.PersonEvent.AppLanguageChanged)

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

        verifyOrder {
            interactor.fetchPerson(10.0)
            interactor.flushPersonData()
            interactor.fetchPerson(10.0)
        }
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

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        lvInteractorEvents.postValue(PersonInteractor.PersonEvent.Success(person))

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
        assertEquals(R.string.person_no_details, viewStatePosted?.contentViewState?.dataAvailable?.titleRes)
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

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(PersonParam(10.0, "aPerson", "aUrl"))

        lvInteractorEvents.postValue(PersonInteractor.PersonEvent.Success(person))

        assertNotNull(viewStatePosted)
        assertEquals("aPerson", viewStatePosted?.screenTitle)
        assertEquals("aUrl", viewStatePosted?.personImageUrl)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.dataAvailable?.visibility)

        // birthday
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.birthday?.visibility)
        assertEquals(R.string.person_birthday_title, viewStatePosted?.contentViewState?.birthday?.titleRes)
        assertEquals("aBirthday", viewStatePosted?.contentViewState?.birthday?.value)

        // place of birth
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.placeOfBirth?.visibility)
        assertEquals(R.string.person_birth_place_title, viewStatePosted?.contentViewState?.placeOfBirth?.titleRes)
        assertEquals("aPlaceOfBirth", viewStatePosted?.contentViewState?.placeOfBirth?.value)

        // deathday
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.deathDay?.visibility)
        assertEquals(R.string.person_death_day_title, viewStatePosted?.contentViewState?.deathDay?.titleRes)
        assertEquals("aDeathday", viewStatePosted?.contentViewState?.deathDay?.value)

        // bio
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.bio?.visibility)
        assertEquals(R.string.person_bio_title, viewStatePosted?.contentViewState?.bio?.titleRes)
        assertEquals("a bio that is long", viewStatePosted?.contentViewState?.bio?.value)
    }
}