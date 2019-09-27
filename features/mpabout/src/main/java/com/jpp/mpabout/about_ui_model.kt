package com.jpp.mpabout

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/*
 * This file contains the definitions for the entire model used in the about feature.
 */

/**************************************************************************************************
 *************************************** VIEW STATES **********************************************
 **************************************************************************************************/

/**
 * Represents the view state of the about fragment.
 */
data class AboutViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val header: AboutHeader = AboutHeader(),
        val content: AboutContent = AboutContent()) {

    companion object {
        fun showLoading() = AboutViewState()
        fun showContent(
                appVersion: String,
                aboutItems: List<AboutItem>) = AboutViewState(header = AboutHeader.withHeader(appVersion), content = AboutContent.withContent(aboutItems))
    }

}

/**
 * Represents the header section of the about screen.
 */
data class AboutHeader(val visibility: Int = View.INVISIBLE,
                       val icon: Int = R.drawable.ic_launcher_round,
                       val title: Int = R.string.app_name,
                       val appVersion: String = "") {

    companion object {
        fun withHeader(appVersion: String) = AboutHeader(
                visibility = View.VISIBLE,
                appVersion = appVersion
        )
    }

}
/**
 * Represents the content of the about screen.
 */
data class AboutContent(
        val visibility: Int = View.INVISIBLE,
        val contentText: Int = R.string.about_content,
        val aboutItems: List<AboutItem> = emptyList()) {
    companion object {
        fun withContent(aboutItems: List<AboutItem>)
                = AboutContent(
                visibility = View.VISIBLE,
                aboutItems = aboutItems
        )
    }

}

/**
 * Represents a selectable action in the about section.
 */
sealed class AboutItem(@StringRes val title: Int, @DrawableRes val icon: Int) {
    object RateApp : AboutItem(R.string.about_rate_app_action, R.drawable.ic_rate_app)
    object ShareApp : AboutItem(R.string.about_share_app_action, R.drawable.ic_share_app)
    object PrivacyPolicy : AboutItem(R.string.about_privacy_policy_action, R.drawable.ic_app_icon_black)
    object BrowseAppCode : AboutItem(R.string.about_brows_code_action, R.drawable.ic_github_logo)
    object Licenses : AboutItem(R.string.about_open_source_action, R.drawable.ic_open_source_libraries)
    object TheMovieDbTermsOfUse : AboutItem(R.string.about_the_movie_db_terms_of_use, R.drawable.ic_the_movie_db)
}

/**************************************************************************************************
 *************************************** NAVIGATION ***********************************************
 **************************************************************************************************/

/**
 * Represents the navigation events that can be routed in the about section.
 */
sealed class AboutNavEvent {
    data class InnerNavigation(val url: String) : AboutNavEvent()
    data class OpenGooglePlay(val url: String) : AboutNavEvent()
    data class OpenSharing(val url: String) : AboutNavEvent()
    data class OuterNavigation(val url: String) : AboutNavEvent()
}