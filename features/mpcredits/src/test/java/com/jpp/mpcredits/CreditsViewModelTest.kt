package com.jpp.mpcredits

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.CrewMember
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class CreditsViewModelTest {

    @RelaxedMockK
    private lateinit var creditsInteractor: CreditsInteractor

    @MockK
    private lateinit var imagesPathInteractor: ImagesPathInteractor

    private val lvInteractorEvents = MutableLiveData<CreditsInteractor.CreditsEvent>()

    private lateinit var subject: CreditsViewModel

    @BeforeEach
    fun setUp() {
        every { creditsInteractor.events } returns lvInteractorEvents

        val dispatchers = object : CoroutineDispatchers {
            override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
            override fun default(): CoroutineDispatcher = Dispatchers.Unconfined
        }

        subject = CreditsViewModel(
                dispatchers,
                creditsInteractor,
                imagesPathInteractor
        )
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: CreditsViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(CreditsInitParam("aMovie", 10.0, 12))

        lvInteractorEvents.postValue(CreditsInteractor.CreditsEvent.NotConnectedToNetwork)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.creditsViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.noCreditsViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertNotEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should retry to fetch data when not connected and retry is executed`() {
        var viewStatePosted: CreditsViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(CreditsInitParam("aMovie", 10.0, 12))
        lvInteractorEvents.postValue(CreditsInteractor.CreditsEvent.NotConnectedToNetwork)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify(exactly = 2) { creditsInteractor.fetchCreditsForMovie(10.0) }
        } ?: fail()
    }

    @Test
    fun `Should post error when failing to fetch credits`() {
        var viewStatePosted: CreditsViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(CreditsInitParam("aMovie", 10.0, 12))

        lvInteractorEvents.postValue(CreditsInteractor.CreditsEvent.UnknownError)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.creditsViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.noCreditsViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should retry to fetch data when error unknown and retry is executed`() {
        var viewStatePosted: CreditsViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(CreditsInitParam("aMovie", 10.0, 12))
        lvInteractorEvents.postValue(CreditsInteractor.CreditsEvent.UnknownError)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify(exactly = 2) { creditsInteractor.fetchCreditsForMovie(10.0) }
        } ?: fail()
    }

    @Test
    fun `Should post loading, clear data and fetch credits on language changed `() {
        var viewStatePosted: CreditsViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(CreditsInitParam("aMovie", 10.0, 12))
        lvInteractorEvents.postValue(CreditsInteractor.CreditsEvent.AppLanguageChanged)

        assertNotNull(viewStatePosted)
        assertEquals(View.VISIBLE, viewStatePosted?.loadingVisibility)

        assertEquals(View.INVISIBLE, viewStatePosted?.noCreditsViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.creditsViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        verifyOrder {
            creditsInteractor.fetchCreditsForMovie(10.0)
            creditsInteractor.flushCreditsData()
            creditsInteractor.fetchCreditsForMovie(10.0)
        }
    }

    @Test
    fun `Should post loading and fetch person data onInit`() {
        var viewStatePosted: CreditsViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(CreditsInitParam("aMovie", 10.0, 12))

        assertNotNull(viewStatePosted)
        assertEquals(View.VISIBLE, viewStatePosted?.loadingVisibility)

        assertEquals(View.INVISIBLE, viewStatePosted?.noCreditsViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.creditsViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        verify { creditsInteractor.fetchCreditsForMovie(10.0) }
    }

    @Test
    fun `Should post no data available when crew and cast data is empty`() {
        var viewStatePosted: CreditsViewState? = null
        val credits = Credits(
                id = 10.0,
                cast = listOf(),
                crew = listOf()
        )

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(CreditsInitParam("aMovie", 10.0, 12))

        lvInteractorEvents.postValue(CreditsInteractor.CreditsEvent.Success(credits))

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.creditsViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.noCreditsViewState?.visibility)
        assertEquals(R.string.no_credits_for_this_movie, viewStatePosted?.noCreditsViewState?.titleRes)
    }

    @Test
    fun `Should fetch credits, adapt result to UI model and post value`() {
        var viewStatePosted: CreditsViewState? = null
        val credits = Credits(
                id = 12.toDouble(),
                cast = cast,
                crew = crew
        )
        val expectedUiListSize = cast.size + crew.size

        every { imagesPathInteractor.configureCastCharacter(any(), any()) } answers { arg(1) }

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(CreditsInitParam("aMovie", 10.0, 12))
        lvInteractorEvents.postValue(CreditsInteractor.CreditsEvent.Success(credits))

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.noCreditsViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.creditsViewState?.visibility)

        viewStatePosted?.creditsViewState?.creditItems?.let { uiCredits ->
            assertEquals(expectedUiListSize, uiCredits.size)

            for (i in 0..3) {
                assertEquals(cast[i].id, uiCredits[i].id)
                assertEquals(cast[i].profile_path, uiCredits[i].profilePath)
                assertEquals(cast[i].character, uiCredits[i].title)
                assertEquals(cast[i].name, uiCredits[i].subTitle)
            }

            for (i in 4..7) {
                assertEquals(crew[i - 4].id, uiCredits[i].id)
                assertEquals(crew[i - 4].profile_path, uiCredits[i].profilePath)
                assertEquals(crew[i - 4].name, uiCredits[i].title)
                assertEquals(crew[i - 4].department, uiCredits[i].subTitle)
            }
        } ?: fail()
    }

    @Test
    fun `Should update reached destination in onInit`() {
        var destinationReached: Destination? = null
        val expected = Destination.ReachedDestination("aMovie")

        subject.destinationEvents.observeWith { destinationReached = it }

        subject.onInit(CreditsInitParam("aMovie", 10.0, 12))

        assertEquals(expected, destinationReached)
    }

    @Test
    fun `Should request navigation to person details when credit item selected`() {
        val personItem = CreditPerson(
                id = 10.0,
                profilePath = "aProfile",
                title = "aTitle",
                subTitle = "aSubtitle"
        )

        val expectedDestination = Destination.MPPerson(
                personId = "10.0",
                personImageUrl = "aProfile",
                personName = "aSubtitle")

        var requestedDestination: Destination? = null

        subject.navigationEvents.observeWith { it.actionIfNotHandled { dest -> requestedDestination = dest } }
        subject.onCreditItemSelected(personItem)

        assertEquals(expectedDestination, requestedDestination)
    }

    private val cast = listOf(
            CastCharacter(
                    cast_id = 12.toDouble(),
                    character = "aCharacter1",
                    credit_id = "aCredit1",
                    gender = 1,
                    id = 1.toDouble(),
                    name = "aName1",
                    order = 1,
                    profile_path = "aProfilePath1"
            ),
            CastCharacter(
                    cast_id = 12.toDouble(),
                    character = "aCharacter2",
                    credit_id = "aCredit2",
                    gender = 2,
                    id = 2.toDouble(),
                    name = "aName2",
                    order = 2,
                    profile_path = "aProfilePath2"
            ),
            CastCharacter(
                    cast_id = 12.toDouble(),
                    character = "aCharacter3",
                    credit_id = "aCredit3",
                    gender = 3,
                    id = 3.toDouble(),
                    name = "aName3",
                    order = 3,
                    profile_path = "aProfilePath3"
            ),
            CastCharacter(
                    cast_id = 12.toDouble(),
                    character = "aCharacter4",
                    credit_id = "aCredit4",
                    gender = 4,
                    id = 4.toDouble(),
                    name = "aName4",
                    order = 4,
                    profile_path = "aProfilePath4"
            )
    )

    private val crew = listOf(
            CrewMember(
                    credit_id = "aCreditId1",
                    department = "aDepartment1",
                    gender = 1,
                    id = 1.toDouble(),
                    job = "aJob1",
                    name = "aName1",
                    profile_path = "aProfilePath1"
            ),
            CrewMember(
                    credit_id = "aCreditId2",
                    department = "aDepartment2",
                    gender = 2,
                    id = 2.toDouble(),
                    job = "aJob2",
                    name = "aName2",
                    profile_path = "aProfilePath2"
            ),
            CrewMember(
                    credit_id = "aCreditId3",
                    department = "aDepartment3",
                    gender = 3,
                    id = 3.toDouble(),
                    job = "aJob3",
                    name = "aName3",
                    profile_path = "aProfilePath3"
            ),
            CrewMember(
                    credit_id = "aCreditId4",
                    department = "aDepartment4",
                    gender = 4,
                    id = 4.toDouble(),
                    job = "aJob4",
                    name = "aName4",
                    profile_path = "aProfilePath4"
            )
    )
}
