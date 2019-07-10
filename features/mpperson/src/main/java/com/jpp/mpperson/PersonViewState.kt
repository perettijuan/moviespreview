package com.jpp.mpperson

import android.view.View

sealed class PersonViewState(
        val unknownErrorVisibility: Int = View.INVISIBLE,
        val connectivityErrorVisibility: Int = View.INVISIBLE,
        val loadingVisibility: Int = View.INVISIBLE,
        val contentVisibility: Int = View.INVISIBLE) {

    object ShowLoading : PersonViewState(loadingVisibility = View.VISIBLE)
    object ShowUnknownError : PersonViewState(unknownErrorVisibility = View.VISIBLE)
    object ShowNoConnectivityError : PersonViewState(connectivityErrorVisibility = View.VISIBLE)
    object ShowPerson : PersonViewState(contentVisibility = View.VISIBLE)
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