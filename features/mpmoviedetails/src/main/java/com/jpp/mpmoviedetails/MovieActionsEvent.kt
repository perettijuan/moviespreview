package com.jpp.mpmoviedetails

internal sealed class MovieActionsEvent {
    data class ShowUserNotLogged(
        val error: Int = R.string.account_need_to_login,
        val action: Int = R.string.login_generic
    ) : MovieActionsEvent()

    data class ShowUnexpectedError(
        val error: Int = R.string.unexpected_action_error
    ) : MovieActionsEvent()
}
