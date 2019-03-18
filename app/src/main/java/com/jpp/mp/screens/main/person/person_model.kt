package com.jpp.mp.screens.main.person

/**
 * Represents the state of the view that shows the person data.
 */
sealed class PersonViewState {
    object ErrorUnknown : PersonViewState()
    object ErrorNoConnectivity : PersonViewState()
    object LoadedEmpty : PersonViewState()
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
                    val placeOfBirth: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UiPerson

        if (name != other.name) return false
        if (biography != other.biography) return false
        if (birthday != other.birthday) return false
        if (deathday != other.deathday) return false
        if (placeOfBirth != other.placeOfBirth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + biography.hashCode()
        result = 31 * result + birthday.hashCode()
        result = 31 * result + deathday.hashCode()
        result = 31 * result + placeOfBirth.hashCode()
        return result
    }
}