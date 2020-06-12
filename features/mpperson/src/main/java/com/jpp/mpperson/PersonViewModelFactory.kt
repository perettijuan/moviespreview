package com.jpp.mpperson

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * [ViewModelAssistedFactory] to create specific [PersonViewModel] instances
 * with the dependencies provided by Dagger.
 */
class PersonViewModelFactory @Inject constructor(
    private val personInteractor: PersonInteractor,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<PersonViewModel> {

    override fun create(handle: SavedStateHandle): PersonViewModel {
        return PersonViewModel(personInteractor, ioDispatcher)
    }
}