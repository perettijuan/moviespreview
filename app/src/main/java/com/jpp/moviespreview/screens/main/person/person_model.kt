package com.jpp.moviespreview.screens.main.person

/**
 * Represents the state of the view that shows the person data.
 */
sealed class PersonViewState {
    object ErrorUnknown : PersonViewState()
    object ErrorNoConnectivity : PersonViewState()
    data class Loading(val imageUrl: String, val name: String) : PersonViewState()
    data class Loaded(val person: UiPerson, val showBirthday: Boolean, val showDeathDay: Boolean, val showPlaceOfBirth: Boolean) : PersonViewState()
}

/**
 * Represents a person to be rendered by the UI.
 */
data class UiPerson(val name: String,
                    val biography: String,
                    val birthday: String,
                    val deathday: String,
                    val placeOfBirth: String)