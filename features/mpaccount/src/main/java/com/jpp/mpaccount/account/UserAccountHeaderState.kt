package com.jpp.mpaccount.account

import android.view.View
import com.jpp.mpaccount.R

/**
 * Represents the view state of the header section in the user account view.
 */
internal data class UserAccountHeaderState(
    val screenTitle: Int = R.string.account_title,
    val visibility: Int = View.INVISIBLE,
    val userName: String = "",
    val accountName: String = "",
    val avatarViewState: AccountAvatarViewState = AccountAvatarViewState()
) {

    fun withAvatar(
        userName: String,
        accountName: String,
        avatarUrl: String,
        avatarCallback: (() -> Unit)
    ) = UserAccountHeaderState(
        visibility = View.VISIBLE,
        userName = userName,
        accountName = accountName,
        avatarViewState = avatarViewState.createAvatar(avatarUrl, avatarCallback)
    )

    fun withLetter(
        userName: String,
        accountName: String,
        defaultLetter: String
    ) = UserAccountHeaderState(
        visibility = View.VISIBLE,
        userName = userName,
        accountName = accountName,
        avatarViewState = avatarViewState.createLetter(defaultLetter)
    )

    fun hide(): UserAccountHeaderState = copy(visibility = View.GONE)

    companion object {
        fun showLoading() = UserAccountHeaderState(visibility = View.VISIBLE)
    }
}
