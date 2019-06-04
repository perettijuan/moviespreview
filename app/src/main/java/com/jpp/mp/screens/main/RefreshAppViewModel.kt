package com.jpp.mp.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpdomain.usecase.support.RefreshLanguageDataUseCase
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
                                              private val useCaseLanguage: RefreshLanguageDataUseCase)
    : MPScopedViewModel(dispatchers) {

    private val refreshLiveData by lazy { MutableLiveData<Boolean>() }

    fun init() {
        launch {
            refreshLiveData.value = withContext(dispatchers.default()) { useCaseLanguage.shouldRefreshDataInApp() }
        }
    }

    /**
     * Subscribe to updates from this [LiveData] if you're interested in data refresh.
     */
    fun refreshState(): LiveData<Boolean> = refreshLiveData
}