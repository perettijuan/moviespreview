package com.jpp.mpperson

import android.view.View

/**
 * Represents the view state of the profile person screen.
 */
sealed class PersonViewState(
        val errorVisibility: Int = View.INVISIBLE,
        val loadingVisibility: Int = View.INVISIBLE,
        val content: PersonContent = PersonContent()) {

    //TODO JPP render no person data state.
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
}


data class PersonContent(
        val birthday: UIPersonRow = UIPersonRow.NoDataAvailable,
        val placeOfBirth: UIPersonRow = UIPersonRow.NoDataAvailable,
        val deathDay: UIPersonRow = UIPersonRow.NoDataAvailable,
        val bio: UIPersonRow = UIPersonRow.NoDataAvailable
)


sealed class UIPersonRow(val visibility: Int = View.INVISIBLE,
                         val titleRes: Int,
                         val value: String) {

    object NoDataAvailable : UIPersonRow(titleRes = R.string.person_no_details, value = "")

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
