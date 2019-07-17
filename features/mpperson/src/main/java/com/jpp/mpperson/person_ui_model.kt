package com.jpp.mpperson

import android.view.View
import com.jpp.mpperson.PersonErrorViewState.Companion.asConnectivity
import com.jpp.mpperson.PersonErrorViewState.Companion.asUnknownError

/**
 * Represents the view state of the profile person screen.
 */
data class PersonViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: PersonErrorViewState = PersonErrorViewState(),
        val contentViewState: PersonContentViewState = PersonContentViewState()) {

    companion object {
        fun showLoading() = PersonViewState(loadingVisibility = View.VISIBLE)
        fun showUnknownError(errorHandler: () -> Unit) = PersonViewState(errorViewState = asUnknownError(errorHandler))
        fun showNoConnectivityError(errorHandler: () -> Unit) = PersonViewState(errorViewState = asConnectivity(errorHandler))
        fun showPerson(contentViewStateValue: PersonContentViewState) = PersonViewState(contentViewState = contentViewStateValue)
        fun showNoDataAvailable() = PersonViewState(contentViewState = PersonContentViewState(dataAvailable = PersonRowViewState.noDataAvailableRow()))
    }
}

/**
 * Represents the view state of the content of the person biography from the UI perspective.
 */
data class PersonContentViewState(
        val birthday: PersonRowViewState = PersonRowViewState.emptyRow(),
        val placeOfBirth: PersonRowViewState = PersonRowViewState.emptyRow(),
        val deathDay: PersonRowViewState = PersonRowViewState.emptyRow(),
        val bio: PersonRowViewState = PersonRowViewState.emptyRow(),
        val dataAvailable: PersonRowViewState = PersonRowViewState.emptyRow()
)

/**
 * Represents the state of the error view.
 */
data class PersonErrorViewState(val visibility: Int = View.INVISIBLE,
                                val isConnectivity: Boolean = false,
                                val errorHandler: (() -> Unit)? = null) {

    companion object {
        fun asConnectivity(handler: () -> Unit) = PersonErrorViewState(
                visibility = View.VISIBLE,
                isConnectivity = true,
                errorHandler = handler)

        fun asUnknownError(handler: () -> Unit) = PersonErrorViewState(
                visibility = View.VISIBLE,
                isConnectivity = false,
                errorHandler = handler)
    }
}

/**
 * Represents the view state of the rows that are shown in the person's layout.
 */
data class PersonRowViewState(val visibility: Int = View.INVISIBLE,
                              val titleRes: Int,
                              val value: String) {

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
