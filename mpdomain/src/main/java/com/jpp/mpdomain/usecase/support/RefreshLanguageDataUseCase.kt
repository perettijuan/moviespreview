package com.jpp.mpdomain.usecase.support

import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.SupportRepository

/**
 * Defines a UseCase that verifies the inner state of the application in order to know if the
 * current general state being shown to the user needs to be refreshed or not. If the state
 * needs to be refreshed, this UC takes care of clearing out any stored inner state.
 */
interface RefreshLanguageDataUseCase {

    //TODO JPP replace this for RefreshAppDataUseCase

    /**
     * Verifies if the state being shown to the user needs to be refreshed.
     * @return true if the state needs to be refreshed, false any other case.
     */
    fun shouldRefreshDataInApp(): Boolean


    class Impl(private val languageRepository: LanguageRepository,
               private val supportRepository: SupportRepository) : RefreshLanguageDataUseCase {
        override fun shouldRefreshDataInApp(): Boolean {
            return languageRepository.getCurrentAppLanguage().let {
                val refresh = languageRepository.getCurrentDeviceLanguage() != it

                if (refresh) {
                    supportRepository.clearAllData()
                    languageRepository.updateAppLanguage(languageRepository.getCurrentDeviceLanguage()) // always update
                }

                return refresh
            }
        }
    }
}