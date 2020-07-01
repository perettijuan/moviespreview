package com.jpp.mpmoviedetails

import android.view.View

/**
 * Represents the state of the action buttons show to the user.
 */
internal data class ActionButtonState(
    val visibility: Int = View.INVISIBLE,
    val animateLoading: Boolean = false,
    val asFilled: Boolean = false,
    val asClickable: Boolean = false
) {
    fun asVisible(): ActionButtonState {
        return copy(
            visibility = View.VISIBLE,
            asClickable = true
        )
    }

    fun asInVisible(): ActionButtonState {
        return copy(visibility = View.INVISIBLE)
    }

    fun asFilled(): ActionButtonState {
        return copy(
            visibility = View.VISIBLE,
            asFilled = true,
            asClickable = true,
            animateLoading = false
        )
    }

    fun asEmpty(): ActionButtonState {
        return copy(
            visibility = View.VISIBLE,
            asFilled = false,
            asClickable = true,
            animateLoading = false
        )
    }

    fun asLoading(): ActionButtonState {
        return copy(
            animateLoading = true,
            asClickable = false
        )
    }

    fun flipState(): ActionButtonState {
        return copy(
            animateLoading = false,
            asClickable = true,
            asFilled = !this.asFilled
        )
    }
}
