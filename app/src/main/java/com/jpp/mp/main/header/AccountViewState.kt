package com.jpp.mp.main.header

import android.view.View

/**
 * Represents the view state of the account views.
 */
internal data class AccountViewState(
    val visibility: Int = View.GONE,
    val userName: String = "",
    val accountName: String = "",
    val avatarViewState: AccountAvatarViewState = AccountAvatarViewState()
) {
    companion object {
        fun createWithAvatar(
            userName: String,
            accountName: String,
            avatarUrl: String,
            avatarCallback: (() -> Unit)
        ) = AccountViewState(
            visibility = View.VISIBLE,
            userName = userName,
            accountName = accountName,
            avatarViewState = AccountAvatarViewState.createAvatar(avatarUrl, avatarCallback)
        )

        fun createWithDefaultLetter(
            userName: String,
            accountName: String,
            defaultLetter: String
        ) = AccountViewState(
            visibility = View.VISIBLE,
            userName = userName,
            accountName = accountName,
            avatarViewState = AccountAvatarViewState.createLetter(defaultLetter)
        )
    }
}
