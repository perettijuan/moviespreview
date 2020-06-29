package com.jpp.mpperson

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetPersonUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [PersonViewModel] instances
 * with the dependencies provided by Dagger.
 */
class PersonViewModelFactory @Inject constructor(
    private val getPersonUseCase: GetPersonUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<PersonViewModel> {

    override fun create(handle: SavedStateHandle): PersonViewModel {
        return PersonViewModel(getPersonUseCase, ioDispatcher, handle)
    }
}
