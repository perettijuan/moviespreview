package com.jpp.mp.main.header

import android.view.View

/**
 * Represents the view states that the [NavigationHeaderFragment] can render at any given time.
 */
internal data class HeaderViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val loginButtonViewState: LoginButtonViewState = LoginButtonViewState(),
    val accountViewState: AccountViewState = AccountViewState(),
    val detailsViewState: AccountDetailsViewState = AccountDetailsViewState()
) {
    companion object {
        fun showLoading() = HeaderViewState(loadingVisibility = View.VISIBLE)
        fun showLogin() =
            HeaderViewState(loginButtonViewState = LoginButtonViewState(visibility = View.VISIBLE))

        fun showAccountWithAvatar(
            userName: String,
            accountName: String,
            avatarUrl: String,
            avatarCallback: (() -> Unit)
        ) = HeaderViewState(
            accountViewState = AccountViewState.createWithAvatar(
                userName,
                accountName,
                avatarUrl,
                avatarCallback
            ),
            detailsViewState = AccountDetailsViewState(visibility = View.VISIBLE)
        )

        fun showAccountWithLetter(
            userName: String,
            accountName: String,
            defaultLetter: String
        ) = HeaderViewState(
            accountViewState = AccountViewState.createWithDefaultLetter(
                userName,
                accountName,
                defaultLetter
            ),
            detailsViewState = AccountDetailsViewState(visibility = View.VISIBLE)
        )
    }
}
