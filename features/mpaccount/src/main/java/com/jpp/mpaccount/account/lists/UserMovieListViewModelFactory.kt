package com.jpp.mpaccount.account.lists

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetUserAccountMoviePageUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [UserMovieListViewModel] instances
 * with the dependencies provided by Dagger.
 */
class UserMovieListViewModelFactory @Inject constructor(
    private val getMoviesUseCase: GetUserAccountMoviePageUseCase,
    private val navigator: UserMovieListNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<UserMovieListViewModel> {
    override fun create(handle: SavedStateHandle): UserMovieListViewModel {
        return UserMovieListViewModel(getMoviesUseCase, navigator, ioDispatcher, handle)
    }
}
