package com.jpp.mpcredits

import android.view.View
import androidx.lifecycle.SavedStateHandle
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.CrewMember
import com.jpp.mpdomain.usecase.GetCreditsUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
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
class CreditsViewModelTest {

    @MockK
    private lateinit var getCreditsUseCase: GetCreditsUseCase

    @RelaxedMockK
    private lateinit var navigator: CreditNavigator

    private lateinit var subject: CreditsViewModel

    @BeforeEach
    fun setUp() {
        subject = CreditsViewModel(
            getCreditsUseCase,
            navigator,
            CoroutineTestExtension.testDispatcher,
            SavedStateHandle()
        )
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: CreditsViewState? = null

        coEvery { getCreditsUseCase.execute(any()) } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(CreditsInitParam("aMovie", 10.0))

        assertNotNull(viewStatePosted)
        assertEquals("aMovie", viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.creditsViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.noCreditsViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should retry to fetch data when not connected and retry is executed`() {
        var viewStatePosted: CreditsViewState? = null

        coEvery { getCreditsUseCase.execute(any()) } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(CreditsInitParam("aMovie", 10.0))

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getCreditsUseCase.execute(10.0) }
        } ?: fail()
    }

    @Test
    fun `Should post error when failing to fetch credits`() {
        var viewStatePosted: CreditsViewState? = null

        coEvery { getCreditsUseCase.execute(any()) } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(CreditsInitParam("aMovie", 10.0))

        assertNotNull(viewStatePosted)
        assertEquals("aMovie", viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.creditsViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.noCreditsViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should retry to fetch data when error unknown and retry is executed`() {
        var viewStatePosted: CreditsViewState? = null

        coEvery { getCreditsUseCase.execute(any()) } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(CreditsInitParam("aMovie", 10.0))

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getCreditsUseCase.execute(10.0) }
        } ?: fail()
    }

    @Test
    fun `Should post no data available when crew and cast data is empty`() {
        var viewStatePosted: CreditsViewState? = null
        val credits = Credits(
            id = 10.0,
            cast = listOf(),
            crew = listOf()
        )

        coEvery { getCreditsUseCase.execute(any()) } returns Try.Success(credits)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(CreditsInitParam("aMovie", 10.0))

        assertNotNull(viewStatePosted)
        assertEquals("aMovie", viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.creditsViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.noCreditsViewState?.visibility)
        assertEquals(
            R.string.no_credits_for_this_movie,
            viewStatePosted?.noCreditsViewState?.titleRes
        )
    }

    @Test
    fun `Should fetch credits, adapt result to UI model and post value`() {
        var viewStatePosted: CreditsViewState? = null
        val credits = Credits(
            id = 12.0,
            cast = CAST,
            crew = CREW
        )
        val expectedUiListSize = CAST.size + CREW.size

        coEvery { getCreditsUseCase.execute(any()) } returns Try.Success(credits)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(CreditsInitParam("aMovie", 10.0))

        assertNotNull(viewStatePosted)
        assertEquals("aMovie", viewStatePosted?.screenTitle)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.noCreditsViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.creditsViewState?.visibility)

        viewStatePosted?.creditsViewState?.creditItems?.let { uiCredits ->
            assertEquals(expectedUiListSize, uiCredits.size)

            for (i in 0..3) {
                assertEquals(CAST[i].id, uiCredits[i].id)
                assertEquals(CAST[i].profile_path, uiCredits[i].profilePath)
                assertEquals(CAST[i].character, uiCredits[i].title)
                assertEquals(CAST[i].name, uiCredits[i].subTitle)
            }

            for (i in 4..7) {
                assertEquals(CREW[i - 4].id, uiCredits[i].id)
                assertEquals(CREW[i - 4].profile_path, uiCredits[i].profilePath)
                assertEquals(CREW[i - 4].name, uiCredits[i].title)
                assertEquals(CREW[i - 4].department, uiCredits[i].subTitle)
            }
        } ?: fail()
    }

    @Test
    fun `Should request navigation to person details when credit item selected`() {
        val personItem = CreditPerson(
            id = 10.0,
            profilePath = "aProfile",
            title = "aTitle",
            subTitle = "aSubtitle"
        )

        subject.onCreditItemSelected(personItem)

        verify { navigator.navigateToCreditDetail("10.0", "aProfile", "aSubtitle") }
    }

    private companion object {
        private val CAST = listOf(
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter1",
                credit_id = "aCredit1",
                gender = 1,
                id = 1.0,
                name = "aName1",
                order = 1,
                profile_path = "aProfilePath1"
            ),
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter2",
                credit_id = "aCredit2",
                gender = 2,
                id = 2.0,
                name = "aName2",
                order = 2,
                profile_path = "aProfilePath2"
            ),
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter3",
                credit_id = "aCredit3",
                gender = 3,
                id = 3.0,
                name = "aName3",
                order = 3,
                profile_path = "aProfilePath3"
            ),
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter4",
                credit_id = "aCredit4",
                gender = 4,
                id = 4.0,
                name = "aName4",
                order = 4,
                profile_path = "aProfilePath4"
            )
        )

        private val CREW = listOf(
            CrewMember(
                credit_id = "aCreditId1",
                department = "aDepartment1",
                gender = 1,
                id = 1.0,
                job = "aJob1",
                name = "aName1",
                profile_path = "aProfilePath1"
            ),
            CrewMember(
                credit_id = "aCreditId2",
                department = "aDepartment2",
                gender = 2,
                id = 2.0,
                job = "aJob2",
                name = "aName2",
                profile_path = "aProfilePath2"
            ),
            CrewMember(
                credit_id = "aCreditId3",
                department = "aDepartment3",
                gender = 3,
                id = 3.0,
                job = "aJob3",
                name = "aName3",
                profile_path = "aProfilePath3"
            ),
            CrewMember(
                credit_id = "aCreditId4",
                department = "aDepartment4",
                gender = 4,
                id = 4.0,
                job = "aJob4",
                name = "aName4",
                profile_path = "aProfilePath4"
            )
        )
    }
}
