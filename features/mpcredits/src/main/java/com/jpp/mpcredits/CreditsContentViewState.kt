package com.jpp.mpcredits

import android.view.View

/**
 * Represents the state of the credits list in the credits UI.
 */
internal data class CreditsContentViewState(
    val visibility: Int = View.INVISIBLE,
    val creditItems: List<CreditPerson> = listOf()
) {
    fun creditList(items: List<CreditPerson>): CreditsContentViewState = copy(
        visibility = View.VISIBLE,
        creditItems = items
    )
}
