package com.jpp.mpdomain.usecase.support

import com.jpp.mpdomain.repository.LanguageRepository

/**
 * Defines a UseCase that verifies the inner state of the application in order to know if the
 * current general state being shown to the user needs to be refreshed or not.
 */
interface RefreshDataUseCase {

    /**
     * Verifies if the state being shown to the user needs to be refreshed.
     * @return true if the state needs to be refreshed, false any other case.
     */
    fun shouldRefreshDataInApp(): Boolean


    class Impl(private val languageRepository: LanguageRepository) : RefreshDataUseCase {
        override fun shouldRefreshDataInApp(): Boolean {
            return languageRepository.getCurrentAppLanguage()?.let {
                return languageRepository.getCurrentDeviceLanguage() != it
            } ?: run {
                // no app language stored, update
                languageRepository.updateAppLanguage(languageRepository.getCurrentDeviceLanguage())
                false
            }
        }
    }
}