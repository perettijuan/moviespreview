package com.jpp.moviespreview.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Coroutine Scoped ViewModel.
 * Refer to https://github.com/perettijuan/android_cool_coding/blob/master/ArchitectureComponents/app/src/main/java/com/jpp/architecturecomponents/ui/MainActivityViewModel.kt
 * for more information.
 */
abstract class MPScopedViewModel : ViewModel() {

    private val currentJob = Job()
    private val scope: CoroutineScope = CoroutineScope(currentJob + kotlinx.coroutines.Dispatchers.Main)

    protected fun launchInScope(scopedFunction: suspend CoroutineScope.() -> Unit) {
        scope.launch {
            scopedFunction.invoke(this)
        }
    }

    override fun onCleared() {
        currentJob.cancel()
        Log.d("ViewModel", "Scope is active " + (scope.isActive))
        super.onCleared()
    }
}