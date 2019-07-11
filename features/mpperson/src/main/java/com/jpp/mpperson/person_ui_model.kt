package com.jpp.mpperson

import android.view.View

/*
 * This file contains the data definition classes that helps to render the data in the screen.
 * There's a 1 to 1 matching between each one of these classes and each view shown in the
 * person data screen.
 */

/**
 * Represents the view state of the profile person screen.
 */
sealed class PersonViewState(
        val errorState: ErrorState = ErrorState(),
        val loadingVisibility: Int = View.INVISIBLE,
        val content: PersonContent = PersonContent()) {
    /*
     * Used when the loading screen needs to be shown.
     */
    object ShowLoading : PersonViewState(loadingVisibility = View.VISIBLE)

    /*
     * Used when the no connectivity state needs to be rendered
     */
    data class ShowError(val type: ErrorState) : PersonViewState(errorState = type)

    /*
     * Used when the person data can be rendered properly.
     */
    data class ShowPerson(val contentValue: PersonContent) : PersonViewState(content = contentValue)

    /*
     * Used when the data retrieved for the person is completely empty.
     */
    object ShowNoDataAvailable : PersonViewState(content = PersonContent(dataAvailable = PersonRow.NoDataAvailable))
}

/**
 * Represents the content of the person biography from the UI perspective.
 */
data class PersonContent(
        val birthday: PersonRow = PersonRow.EmptyRow,
        val placeOfBirth: PersonRow = PersonRow.EmptyRow,
        val deathDay: PersonRow = PersonRow.EmptyRow,
        val bio: PersonRow = PersonRow.EmptyRow,
        val dataAvailable: PersonRow = PersonRow.EmptyRow
)

class ErrorState(val visibility: Int = View.INVISIBLE,
                 val isConnectivity: Boolean = false,
                 val errorHandler: (() -> Unit)? = null) {

    companion object {
        fun asConnectivity(handler: () -> Unit) = ErrorState(
                visibility = View.VISIBLE,
                isConnectivity = true,
                errorHandler = handler)

        fun asUnknownError(handler: () -> Unit) = ErrorState(
                visibility = View.VISIBLE,
                isConnectivity = false,
                errorHandler = handler)
    }
}

/**
 * Each one of this subclasses represents a row in the UI.
 */
sealed class PersonRow(val visibility: Int = View.INVISIBLE,
                       val titleRes: Int,
                       val value: String) {

    object EmptyRow : PersonRow(titleRes = R.string.person_empty_data, value = "")

    object NoDataAvailable : PersonRow(visibility = View.VISIBLE, titleRes = R.string.person_no_details, value = "")

    data class Birthday(val birthdayValue: String) : PersonRow(
            visibility = View.VISIBLE,
            titleRes = R.string.person_birthday_title,
            value = birthdayValue)

    data class PlaceOfBirth(val placeOfBirthValue: String) : PersonRow(
            visibility = View.VISIBLE,
            titleRes = R.string.person_birth_place_title,
            value = placeOfBirthValue)

    data class DeathDay(val deathDayValue: String) : PersonRow(
            visibility = View.VISIBLE,
            titleRes = R.string.person_death_day_title,
            value = deathDayValue
    )

    data class Bio(val bioContent: String) : PersonRow(
            visibility = View.VISIBLE,
            titleRes = R.string.person_bio_title,
            value = bioContent
    )
}
