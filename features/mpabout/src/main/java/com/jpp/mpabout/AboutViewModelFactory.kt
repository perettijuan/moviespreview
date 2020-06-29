package com.jpp.mpabout

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.repository.AboutUrlRepository
import com.jpp.mpdomain.repository.AppVersionRepository
import javax.inject.Inject

/**
 * [ViewModelAssistedFactory] to create specific [AboutViewModel] instances
 * with the dependencies provided by Dagger.
 */
class AboutViewModelFactory @Inject constructor(
    private val appVersionRepository: AppVersionRepository,
    private val aboutUrlRepository: AboutUrlRepository,
    private val aboutNavigator: AboutNavigator
) : ViewModelAssistedFactory<AboutViewModel> {
    override fun create(handle: SavedStateHandle): AboutViewModel {
        return AboutViewModel(appVersionRepository, aboutUrlRepository, aboutNavigator)
    }
}
