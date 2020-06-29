package com.jpp.mp.main

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdata.datasources.language.LanguageMonitor
import com.jpp.mpdomain.repository.LanguageRepository
import javax.inject.Inject

/**
 * [ViewModelAssistedFactory] to create specific [MainActivityViewModel] instances
 * with the dependencies provided by Dagger.
 */
class MainActivityViewModelFactory @Inject constructor(
    private val languageMonitor: LanguageMonitor,
    private val languageRepository: LanguageRepository
) : ViewModelAssistedFactory<MainActivityViewModel> {
    override fun create(handle: SavedStateHandle): MainActivityViewModel {
        return MainActivityViewModel(languageMonitor, languageRepository)
    }
}
