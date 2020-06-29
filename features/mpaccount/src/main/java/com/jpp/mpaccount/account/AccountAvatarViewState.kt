package com.jpp.mpaccount.account

import android.view.View

/**
 * ViewState that represents the state of the avatar. If the avatar can be downloaded
 * as an image, then [avatarVisibility] will view [View.VISIBLE] and the default letter
 * will be hidden. If there's an error when the avatar is being downloaded, then the
 * view state will show the default letter and will hide the avatar.
 */
internal data class AccountAvatarViewState(
    val avatarUrl: String? = null,
    val avatarVisibility: Int = View.INVISIBLE,
    val avatarErrorCallback: (() -> Unit)? = null,
    val defaultLetter: String = "",
    val defaultLetterVisibility: Int = View.INVISIBLE
) {
    fun createAvatar(avatarUrl: String, callback: (() -> Unit)): AccountAvatarViewState = copy(
        avatarUrl = avatarUrl,
        avatarVisibility = View.VISIBLE,
        avatarErrorCallback = callback
    )

    fun createLetter(defaultLetter: String): AccountAvatarViewState = copy(
        defaultLetter = defaultLetter,
        defaultLetterVisibility = View.VISIBLE,
        avatarVisibility = View.INVISIBLE
    )
}
