package com.jpp.moviespreview.screens.main.about

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jpp.moviespreview.R

/**
 * Represents the view state of the about fragment.
 */
sealed class AboutViewState {
    data class InitialContent(val appVersion: String, val aboutItems: List<AboutItem>) : AboutViewState()
}

/**
 * Represents the navigation events that can be routed in the about section.
 */
sealed class AboutNavEvent {
    data class InnerNavigation(val url: String) : AboutNavEvent()
    data class OpenGooglePlay(val url: String) : AboutNavEvent()
    data class OpenSharing(val url: String) : AboutNavEvent()
    object GoToLicenses : AboutNavEvent()
}

/**
 * Represents a selectable action in the about section.
 */
sealed class AboutItem(@StringRes val title: Int, @DrawableRes val icon: Int) {
    object RateApp : AboutItem(R.string.about_rate_app_action, R.drawable.ic_rate_app)
    object ShareApp : AboutItem(R.string.about_share_app_action, R.drawable.ic_share_app)
    object BrowseAppCode : AboutItem(R.string.about_brows_code_action, R.drawable.ic_github_logo)
    object Licenses : AboutItem(R.string.about_open_source_action, R.drawable.ic_open_source_libraries)
    object TheMovieDbTermsOfUse : AboutItem(R.string.about_the_movie_db_terms_of_use, R.drawable.ic_the_movie_db)
}