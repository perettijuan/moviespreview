package com.jpp.mpperson

/**
 * Represents the view state of the content of the person biography from the UI perspective.
 */
internal data class PersonContentViewState(
    val birthday: PersonRowViewState = PersonRowViewState.emptyRow(),
    val placeOfBirth: PersonRowViewState = PersonRowViewState.emptyRow(),
    val deathDay: PersonRowViewState = PersonRowViewState.emptyRow(),
    val bio: PersonRowViewState = PersonRowViewState.emptyRow(),
    val dataAvailable: PersonRowViewState = PersonRowViewState.emptyRow()
)
