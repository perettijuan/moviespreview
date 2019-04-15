package com.jpp.mp.screens.main.person

import androidx.lifecycle.Observer
import com.jpp.mp.utiltest.InstantTaskExecutorExtension
import com.jpp.mp.utiltest.resumedLifecycleOwner
import com.jpp.mp.screens.main.TestCoroutineDispatchers
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.usecase.person.GetPersonUseCase
import com.jpp.mpdomain.usecase.person.GetPersonResult
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class PersonViewModelTest {

    @MockK
    private lateinit var getPersonUseCase: GetPersonUseCase

    private lateinit var subject: PersonViewModel

    @BeforeEach
    fun setUp() {
        subject = PersonViewModel(TestCoroutineDispatchers(), getPersonUseCase)
    }

    @ParameterizedTest
    @MethodSource("personInput")
    fun `Should execute GetPersonUseCase, adapt result to UI model and post value on init`(input: PersonTestInput) {
        val viewStatePosted = mutableListOf<PersonViewState>()

        val personId = 112.toDouble()
        val personImageUrl = "aUrl"
        val personName = "Han Solo"

        every { getPersonUseCase.getPerson(personId) } returns GetPersonResult.Success(input.domainPerson)

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(personId, personImageUrl, personName)

        assertTrue(viewStatePosted[0] is PersonViewState.Loading)

        with(viewStatePosted[0] as PersonViewState.Loading) {
            assertEquals(personImageUrl, imageUrl)
            assertEquals(personName, name)
        }

        assertTrue(viewStatePosted[1] is PersonViewState.Loaded)

        with(viewStatePosted[1] as PersonViewState.Loaded) {
            assertEquals(input.expectedUiPerson, person)
            assertEquals(input.expectedShowBirthday, showBirthday)
            assertEquals(input.expectedShowDeathDay, showDeathDay)
            assertEquals(input.expectedShowPlaceOfBirth, showPlaceOfBirth)
        }
    }

    @Test
    fun `Should render empty person data`() {
        val viewStatePosted = mutableListOf<PersonViewState>()

        val personId = 112.toDouble()
        val personImageUrl = "aUrl"
        val personName = "Han Solo"

        val domainPerson = Person(
                id = 112.toDouble(),
                name = "aName",
                biography = "",
                birthday = null,
                deathday = null,
                place_of_birth = null
        )

        every { getPersonUseCase.getPerson(personId) } returns GetPersonResult.Success(domainPerson)

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(personId, personImageUrl, personName)

        assertTrue(viewStatePosted[1] is PersonViewState.LoadedEmpty)
    }

    @Test
    fun `Should execute GetPersonUseCase and show connectivity error`() {
        val viewStatePosted = mutableListOf<PersonViewState>()

        every { getPersonUseCase.getPerson(any()) } returns GetPersonResult.ErrorNoConnectivity

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(121.toDouble(), "", "")

        assertTrue(viewStatePosted[0] is PersonViewState.Loading)
        assertTrue(viewStatePosted[1] is PersonViewState.ErrorNoConnectivity)
    }

    @Test
    fun `Should execute GetPersonUseCase and show unknown error`() {
        val viewStatePosted = mutableListOf<PersonViewState>()

        every { getPersonUseCase.getPerson(any()) } returns GetPersonResult.ErrorUnknown

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(121.toDouble(), "", "")

        assertTrue(viewStatePosted[0] is PersonViewState.Loading)
        assertTrue(viewStatePosted[1] is PersonViewState.ErrorUnknown)
    }

    data class PersonTestInput(
            val domainPerson: Person,
            val expectedUiPerson: UiPerson,
            val expectedShowBirthday: Boolean,
            val expectedShowDeathDay: Boolean,
            val expectedShowPlaceOfBirth: Boolean
    )


    companion object {

        @JvmStatic
        fun personInput() = listOf(
                PersonTestInput(
                        domainPerson = Person(
                                id = 112.toDouble(),
                                name = "aName",
                                biography = "aBio",
                                birthday = "aBirthday",
                                deathday = "aDeathDay",
                                place_of_birth = "In a galaxy far far away"
                        ),
                        expectedUiPerson = UiPerson(
                                name = "aName",
                                biography = "aBio",
                                birthday = "aBirthday",
                                deathday = "aDeathDay",
                                placeOfBirth = "In a galaxy far far away"
                        ),
                        expectedShowBirthday = true,
                        expectedShowDeathDay = true,
                        expectedShowPlaceOfBirth = true
                ),
                PersonTestInput(
                        domainPerson = Person(
                                id = 112.toDouble(),
                                name = "aName",
                                biography = "aBio",
                                birthday = null,
                                deathday = "aDeathDay",
                                place_of_birth = "In a galaxy far far away"
                        ),
                        expectedUiPerson = UiPerson(
                                name = "aName",
                                biography = "aBio",
                                birthday = "",
                                deathday = "aDeathDay",
                                placeOfBirth = "In a galaxy far far away"
                        ),
                        expectedShowBirthday = false,
                        expectedShowDeathDay = true,
                        expectedShowPlaceOfBirth = true
                ),
                PersonTestInput(
                        domainPerson = Person(
                                id = 112.toDouble(),
                                name = "aName",
                                biography = "aBio",
                                birthday = "aBirthday",
                                deathday = null,
                                place_of_birth = "In a galaxy far far away"
                        ),
                        expectedUiPerson = UiPerson(
                                name = "aName",
                                biography = "aBio",
                                birthday = "aBirthday",
                                deathday = "",
                                placeOfBirth = "In a galaxy far far away"
                        ),
                        expectedShowBirthday = true,
                        expectedShowDeathDay = false,
                        expectedShowPlaceOfBirth = true
                ),
                PersonTestInput(
                        domainPerson = Person(
                                id = 112.toDouble(),
                                name = "aName",
                                biography = "aBio",
                                birthday = "aBirthday",
                                deathday = "aDeathDay",
                                place_of_birth = null
                        ),
                        expectedUiPerson = UiPerson(
                                name = "aName",
                                biography = "aBio",
                                birthday = "aBirthday",
                                deathday = "aDeathDay",
                                placeOfBirth = ""
                        ),
                        expectedShowBirthday = true,
                        expectedShowDeathDay = true,
                        expectedShowPlaceOfBirth = false
                ),
                PersonTestInput(
                        domainPerson = Person(
                                id = 112.toDouble(),
                                name = "aName",
                                biography = "aBio",
                                birthday = null,
                                deathday = null,
                                place_of_birth = null
                        ),
                        expectedUiPerson = UiPerson(
                                name = "aName",
                                biography = "aBio",
                                birthday = "",
                                deathday = "",
                                placeOfBirth = ""
                        ),
                        expectedShowBirthday = false,
                        expectedShowDeathDay = false,
                        expectedShowPlaceOfBirth = false
                )
        )
    }
}