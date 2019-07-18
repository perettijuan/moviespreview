package com.jpp.mpdomain.usecase.about

import com.jpp.mpdomain.repository.AboutUrlRepository


/**
 * Defines a UseCase that retrieves the URLs that are needed for navigate the content in the
 * about section of the application.
 */
//TODO delete ME
interface GetAboutNavigationUrlUseCase {

    /**
     * @return a String that represents the URL needed to navigate the section represented by
     * [navigationType].
     */
    fun getUrlFor(navigationType: AboutNavigationType): String

    class Impl(private val repository: AboutUrlRepository) : GetAboutNavigationUrlUseCase {

        override fun getUrlFor(navigationType: AboutNavigationType): String {
            return when (navigationType) {
                is AboutNavigationType.TheMovieDbTermsOfUse -> repository.getTheMovieDbTermOfUseUrl().url
                is AboutNavigationType.AppCodeRepo -> repository.getCodeRepoUrl().url
                is AboutNavigationType.GooglePlayApp -> repository.getGPlayAppUrl().url
                is AboutNavigationType.GooglePlayWeb -> repository.getGPlayWebUrl().url
                is AboutNavigationType.ShareApp -> repository.getSharingUrl().url
                is AboutNavigationType.PrivacyPolicy -> repository.getPrivacyPolicyUrl().url
            }
        }
    }
}