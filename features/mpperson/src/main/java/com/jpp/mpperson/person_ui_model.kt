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
        val errorVisibility: Int = View.INVISIBLE,
        val loadingVisibility: Int = View.INVISIBLE,
        val content: PersonContent = PersonContent()) {
    /*
     * Used when the loading screen needs to be shown.
     */
    object ShowLoading : PersonViewState(loadingVisibility = View.VISIBLE)

    /*
     * Used when an unknown error state needs to be rendered TODO JPP render and react properly
     */
    object ShowUnknownError : PersonViewState(errorVisibility = View.VISIBLE)

    /*
     * Used when the no connectivity state needs to be rendered TODO JPP render and react properly
     */
    object ShowNoConnectivity : PersonViewState(errorVisibility = View.VISIBLE)

    /*
     * Used when the person data can be rendered properly.
     */
    data class ShowPerson(val contentValue: PersonContent) : PersonViewState(content = contentValue)

    object ShowNoDataAvailable : PersonViewState(content = PersonContent(dataAvailable = UIPersonRow.NoDataAvailable))
}

/**
 * Represents the content of the person biography from the UI perspective.
 */
data class PersonContent(
        val birthday: UIPersonRow = UIPersonRow.EmptyRow,
        val placeOfBirth: UIPersonRow = UIPersonRow.EmptyRow,
        val deathDay: UIPersonRow = UIPersonRow.EmptyRow,
        val bio: UIPersonRow = UIPersonRow.EmptyRow,
        val dataAvailable: UIPersonRow = UIPersonRow.EmptyRow
)

/**
 * Each one of this subclasses represents a row in the UI.
 */
sealed class UIPersonRow(val visibility: Int = View.INVISIBLE,
                         val titleRes: Int,
                         val value: String) {

    object EmptyRow : UIPersonRow(titleRes = R.string.person_empty_data, value = "")

    object NoDataAvailable : UIPersonRow(visibility = View.VISIBLE, titleRes = R.string.person_no_details, value = "")

    data class Birthday(val birthdayValue: String) : UIPersonRow(
            visibility = View.VISIBLE,
            titleRes = R.string.person_birthday_title,
            value = birthdayValue)

    data class PlaceOfBirth(val placeOfBirthValue: String) : UIPersonRow(
            visibility = View.VISIBLE,
            titleRes = R.string.person_birth_place_title,
            value = placeOfBirthValue)

    data class DeathDay(val deathDayValue: String) : UIPersonRow(
            visibility = View.VISIBLE,
            titleRes = R.string.person_death_day_title,
            value = deathDayValue
    )

    data class Bio(val bioContent: String) : UIPersonRow(
            visibility = View.VISIBLE,
            titleRes = R.string.person_bio_title,
            value = bioContent
    )
}
