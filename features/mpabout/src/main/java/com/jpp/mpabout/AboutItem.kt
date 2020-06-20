package com.jpp.mpabout

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Represents a selectable action in the about section.
 */
internal sealed class AboutItem(@StringRes val title: Int, @DrawableRes val icon: Int) {
    object RateApp : AboutItem(R.string.about_rate_app_action, R.drawable.ic_rate_app)
    object ShareApp : AboutItem(R.string.about_share_app_action, R.drawable.ic_share_app)
    object PrivacyPolicy : AboutItem(R.string.about_privacy_policy_action, R.drawable.ic_app_icon_black)
    object BrowseAppCode : AboutItem(R.string.about_brows_code_action, R.drawable.ic_github_logo)
    object Licenses : AboutItem(R.string.about_open_source_action, R.drawable.ic_open_source_libraries)
    object TheMovieDbTermsOfUse : AboutItem(R.string.about_the_movie_db_terms_of_use, R.drawable.ic_the_movie_db)
}
