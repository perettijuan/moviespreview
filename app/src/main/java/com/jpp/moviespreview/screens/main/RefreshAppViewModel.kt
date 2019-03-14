package com.jpp.moviespreview.screens.main

import androidx.lifecycle.LiveData
import com.jpp.moviespreview.screens.CoroutineDispatchers
import com.jpp.moviespreview.screens.MPScopedViewModel
import com.jpp.moviespreview.screens.SingleLiveEvent
import com.jpp.mpdomain.usecase.support.RefreshDataUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Special ViewModel shared between the MainActivity and the contained Fragments.
 * Under some circumstances, the data that is currently shown to the user needs to be
 * refreshed (for instance, the user changes the device language).
 * This ViewModel serves to those cases: the Fragments are subscribed to the refreshState()
 * LiveData and execute some logic when the UC detects that the data needs to be refreshed.
 */
class RefreshAppViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                              private val useCase: RefreshDataUseCase)
    : MPScopedViewModel(dispatchers) {

    private val refreshLiveData by lazy { SingleLiveEvent<Boolean>() }

    fun init() {
        launch {
            refreshLiveData.value = withContext(dispatchers.default()) { useCase.shouldRefreshDataInApp() }
        }
    }


    fun refreshState(): LiveData<Boolean> = refreshLiveData
}