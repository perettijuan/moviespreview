package com.jpp.mpdomain.usecase.about

import com.jpp.mpdomain.repository.AboutNavigationRepository


/**
 * Defines a UseCase that retrieves the URLs that are needed for navigate the content in the
 * about section of the application.
 *
 * Note: Untested for simplicity.
 */
interface GetAboutNavigationUrlUseCase {

    /**
     * @return a String that represents the URL needed to navigate the section represented by
     * [navigationType].
     */
    fun getUrlFor(navigationType: AboutNavigationType): String

    class Impl(private val repository: AboutNavigationRepository) : GetAboutNavigationUrlUseCase {

        override fun getUrlFor(navigationType: AboutNavigationType): String {
            return when (navigationType) {
                is AboutNavigationType.TheMovieDbTermsOfUse -> repository.getTheMovieDbTermOfUseUrl()
                is AboutNavigationType.AppCodeRepo -> repository.getCodeRepoUrl()
                is AboutNavigationType.GooglePlayApp -> repository.getGPlayAppUrl()
                is AboutNavigationType.GooglePlayWeb -> repository.getGPlayWebUrl()
                is AboutNavigationType.ShareApp -> repository.getSharingUrl()
            }
        }
    }
}