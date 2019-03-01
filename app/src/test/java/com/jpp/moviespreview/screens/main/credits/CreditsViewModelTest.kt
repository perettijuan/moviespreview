package com.jpp.moviespreview.screens.main.credits

import androidx.lifecycle.Observer
import com.jpp.moviespreview.InstantTaskExecutorExtension
import com.jpp.moviespreview.resumedLifecycleOwner
import com.jpp.moviespreview.screens.main.TestCoroutineDispatchers
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.CrewMember
import com.jpp.mpdomain.usecase.credits.ConfigCastCharacterUseCase
import com.jpp.mpdomain.usecase.credits.GetCreditsResult
import com.jpp.mpdomain.usecase.credits.GetCreditsUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class CreditsViewModelTest {

    @MockK
    private lateinit var getCreditsUseCase: GetCreditsUseCase
    @MockK
    private lateinit var configCastCharacterUseCase: ConfigCastCharacterUseCase

    private lateinit var subject: CreditsViewModel
    private val movieId = 121.toDouble()
    private val targetImageSize = 200

    @BeforeEach
    fun setUp() {
        subject = CreditsViewModel(
                TestCoroutineDispatchers(),
                getCreditsUseCase,
                configCastCharacterUseCase
        )
    }

    @Test
    fun `Should execute use cases, adapt result to UI model and post value on init`() {
        val viewStatePosted = mutableListOf<CreditsViewState>()
        val credits = Credits(
                id = 12.toDouble(),
                cast = cast,
                crew = crew
        )
        val expectedUiListSize = cast.size + crew.size

        every { getCreditsUseCase.getCreditsForMovie(any()) } returns GetCreditsResult.Success(credits)
        every { configCastCharacterUseCase.configure(any(), any()) } answers { arg(1) }

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(movieId, targetImageSize)

        // states are posted in correct order
        assertTrue(viewStatePosted[0] is CreditsViewState.Loading)
        assertTrue(viewStatePosted[1] is CreditsViewState.ShowCredits)

        val uiCredits = (viewStatePosted[1] as CreditsViewState.ShowCredits).credits

        // list items are mapped correctly
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

        // use cases are executed
        verify(exactly = 1) { getCreditsUseCase.getCreditsForMovie(movieId) }
        verify(exactly = cast.size) { configCastCharacterUseCase.configure(targetImageSize, any()) }
    }


    @Test
    fun `Should execute use and show connectivity error`() {
        val viewStatePosted = mutableListOf<CreditsViewState>()

        every { getCreditsUseCase.getCreditsForMovie(any()) } returns GetCreditsResult.ErrorNoConnectivity

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(movieId, targetImageSize)

        assertTrue(viewStatePosted[0] is CreditsViewState.Loading)
        assertTrue(viewStatePosted[1] is CreditsViewState.ErrorNoConnectivity)
    }

    @Test
    fun `Should execute use and show unknown error`() {
        val viewStatePosted = mutableListOf<CreditsViewState>()

        every { getCreditsUseCase.getCreditsForMovie(any()) } returns GetCreditsResult.ErrorUnknown

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(movieId, targetImageSize)

        assertTrue(viewStatePosted[0] is CreditsViewState.Loading)
        assertTrue(viewStatePosted[1] is CreditsViewState.ErrorUnknown)
    }

    @Test
    fun `Should retry if state is error unknown`() {
        every { getCreditsUseCase.getCreditsForMovie(any()) } returns GetCreditsResult.ErrorUnknown

        subject.init(movieId, targetImageSize)
        subject.retry()

        verify(exactly = 2) { getCreditsUseCase.getCreditsForMovie(movieId) }
    }

    @Test
    fun `Should retry if state is error connectivity`() {
        every { getCreditsUseCase.getCreditsForMovie(any()) } returns GetCreditsResult.ErrorNoConnectivity

        subject.init(movieId, targetImageSize)
        subject.retry()

        verify(exactly = 2) { getCreditsUseCase.getCreditsForMovie(movieId) }
    }

    @Test
    fun `Should not retry if state is success`() {
        val credits = Credits(
                id = 12.toDouble(),
                cast = cast,
                crew = crew
        )

        every { getCreditsUseCase.getCreditsForMovie(any()) } returns GetCreditsResult.Success(credits)
        every { configCastCharacterUseCase.configure(any(), any()) } answers { arg(1) }

        subject.init(movieId, targetImageSize)
        subject.retry()

        verify(exactly = 1) { getCreditsUseCase.getCreditsForMovie(movieId) }
    }


    private val cast by lazy {
        listOf(
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
    }

    private val crew by lazy {
        listOf(
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
}