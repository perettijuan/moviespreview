package com.jpp.mpperson

import android.view.View

/**
 * Represents the view state of the rows that are shown in the person's layout.
 */
internal data class PersonRowViewState(
    val visibility: Int = View.INVISIBLE,
    val titleRes: Int,
    val value: String
) {

    companion object {
        fun emptyRow() = PersonRowViewState(
            titleRes = R.string.person_empty_data,
            value = ""
        )

        fun noDataAvailableRow() = PersonRowViewState(
            visibility = View.VISIBLE,
            titleRes = R.string.person_no_details,
            value = ""
        )

        fun birthdayRow(birthdayValue: String) = PersonRowViewState(
            visibility = View.VISIBLE,
            titleRes = R.string.person_birthday_title,
            value = birthdayValue
        )

        fun placeOfBirthRow(placeOfBirthValue: String) = PersonRowViewState(
            visibility = View.VISIBLE,
            titleRes = R.string.person_birth_place_title,
            value = placeOfBirthValue)

        fun deathDayRow(deathDayValue: String) = PersonRowViewState(
            visibility = View.VISIBLE,
            titleRes = R.string.person_death_day_title,
            value = deathDayValue
        )

        fun bioRow(bioContent: String) = PersonRowViewState(
            visibility = View.VISIBLE,
            titleRes = R.string.person_bio_title,
            value = bioContent
        )
    }
}
