package com.jpp.mp.main.header

import android.view.View
import androidx.annotation.StringRes
import com.jpp.mp.R

/*
 * This file contains the definitions for the entire model used in the navigation header.
 */

/**************************************************************************************************
 *************************************** VIEW STATES **********************************************
 **************************************************************************************************/

/**
 * Represents the view states that the [NavigationHeaderFragment] can render at any given time.
 */
data class HeaderViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val loginButtonViewState: LoginButtonViewState = LoginButtonViewState(),
        val accountViewState: AccountViewState = AccountViewState(),
        val detailsViewState: AccountDetailsViewState = AccountDetailsViewState()
) {
    companion object {
        fun showLoading() = HeaderViewState(loadingVisibility = View.VISIBLE)
        fun showLogin() = HeaderViewState(loginButtonViewState = LoginButtonViewState(visibility = View.VISIBLE))
        fun showAccountWithAvatar(userName: String,
                                  accountName: String,
                                  avatarUrl: String,
                                  avatarCallback: (() -> Unit)) = HeaderViewState(
                accountViewState = AccountViewState.createWithAvatar(
                        userName,
                        accountName,
                        avatarUrl,
                        avatarCallback
                ),
                detailsViewState = AccountDetailsViewState(visibility = View.VISIBLE)
        )

        fun showAccountWithLetter(userName: String,
                                  accountName: String,
                                  defaultLetter: String) = HeaderViewState(
                accountViewState = AccountViewState.createWithDefaultLetter(
                        userName,
                        accountName,
                        defaultLetter
                ),
                detailsViewState = AccountDetailsViewState(visibility = View.VISIBLE)
        )
    }
}

/**
 * Represents the view state of the login button.
 */
data class LoginButtonViewState(
        val visibility: Int = View.GONE,
        @StringRes val title: Int = R.string.nav_header_login
)

/**
 * Represents the view state of the account details section.
 */
data class AccountDetailsViewState(
        val visibility: Int = View.GONE,
        @StringRes val title: Int = R.string.nav_header_to_account
)

/**
 * Represents the view state of the account views.
 */
data class AccountViewState(
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
                avatarCallback: (() -> Unit)) = AccountViewState(
                visibility = View.VISIBLE,
                userName = userName,
                accountName = accountName,
                avatarViewState = AccountAvatarViewState.createAvatar(avatarUrl, avatarCallback)
        )

        fun createWithDefaultLetter(userName: String,
                                    accountName: String,
                                    defaultLetter: String) = AccountViewState(
                visibility = View.VISIBLE,
                userName = userName,
                accountName = accountName,
                avatarViewState = AccountAvatarViewState.createLetter(defaultLetter)
        )
    }
}

/**
 * ViewState that represents the state of the avatar. If the avatar can be downloaded
 * as an image, then [avatarVisibility] will view [View.VISIBLE] and the default letter
 * will be hidden. If there's an error when the avatar is being downloaded, then the
 * view state will show the default letter and will hide the avatar.
 */
data class AccountAvatarViewState(
        val avatarUrl: String? = null,
        val avatarVisibility: Int = View.GONE,
        val avatarErrorCallback: (() -> Unit)? = null,
        val defaultLetter: String = "",
        val defaultLetterVisibility: Int = View.GONE
) {
    companion object {
        fun createAvatar(avatarUrl: String, callback: (() -> Unit)) = AccountAvatarViewState(avatarUrl = avatarUrl, avatarVisibility = View.VISIBLE, avatarErrorCallback = callback)
        fun createLetter(defaultLetter: String) = AccountAvatarViewState(defaultLetter = defaultLetter, defaultLetterVisibility = View.VISIBLE, avatarVisibility = View.INVISIBLE)
    }
}

/**************************************************************************************************
 *************************************** NAVIGATION ***********************************************
 **************************************************************************************************/

/**
 * Represents the navigation events that can be routed from the navigation header.
 */
sealed class HeaderNavigationEvent {
    object ToUserAccount : HeaderNavigationEvent()
    object ToLogin : HeaderNavigationEvent()
}
