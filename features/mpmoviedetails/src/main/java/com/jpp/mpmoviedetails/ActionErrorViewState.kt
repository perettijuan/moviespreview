package com.jpp.mpmoviedetails

/**
 * Represents the error states that the actions section can assume.
 */
internal sealed class ActionErrorViewState {
    object None : ActionErrorViewState()
    object UserNotLogged : ActionErrorViewState()
    object UnknownError : ActionErrorViewState()
}
