package com.jpp.mpaccount.account

import android.view.View
import com.jpp.mpaccount.R
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view state that the user account view can assume at any given moment.
 */
internal data class UserAccountViewState(
    val screenTitle: Int = R.string.account_title,
    val loadingVisibility: Int = View.INVISIBLE,
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val contentViewState: UserAccountContentViewState = UserAccountContentViewState()
) {

    fun showAccountDataWithAvatar(
        userName: String,
        accountName: String,
        avatarUrl: String,
        avatarCallback: (() -> Unit)
    ): UserAccountViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            contentViewState = contentViewState.withAvatar(
                userName,
                accountName,
                avatarUrl,
                avatarCallback
            )
        )
    }

    fun showAccountDataWithLetter(
        userName: String,
        accountName: String,
        defaultLetter: String
    ) = UserAccountViewState(
        loadingVisibility = View.INVISIBLE,
        contentViewState = contentViewState.withLetter(userName, accountName, defaultLetter)
    )

    fun showNoConnectivityError(errorHandler: () -> Unit): UserAccountViewState = copy(
        loadingVisibility = View.INVISIBLE,
        errorViewState = ErrorViewState.asConnectivity(errorHandler)
    )

    fun showUnknownError(errorHandler: () -> Unit): UserAccountViewState = copy(
        loadingVisibility = View.INVISIBLE,
        errorViewState = ErrorViewState.asUnknownError(errorHandler)
    )

    companion object {
        fun showLoading() = UserAccountViewState(loadingVisibility = View.VISIBLE)


    }
}



