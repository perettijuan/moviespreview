package com.jpp.mp.main.header

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * [ViewModelAssistedFactory] to create specific [NavigationHeaderViewModel] instances
 * with the dependencies provided by Dagger.
 */
@ExperimentalCoroutinesApi
class NavigationHeaderViewModelFactory @Inject constructor(
    private val getUserAccountUseCase: GetUserAccountUseCase,
    private val navigator: HeaderNavigator,
    private val sessionRepository: SessionRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<NavigationHeaderViewModel> {
    override fun create(handle: SavedStateHandle): NavigationHeaderViewModel {
        return NavigationHeaderViewModel(getUserAccountUseCase, navigator, sessionRepository, ioDispatcher)
    }
}
