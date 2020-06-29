package com.jpp.mp.common.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * Generic ViewModel creator instance to create ViewModels.
 * There is one [ViewModelAssistedFactory] per [ViewModel] in order to be
 * able to create the instances needed using Dagger.
 */
interface ViewModelAssistedFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}
