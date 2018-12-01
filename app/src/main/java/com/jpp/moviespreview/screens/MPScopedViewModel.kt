package com.jpp.moviespreview.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.isActive
import kotlin.coroutines.CoroutineContext

/**
 * Coroutine Scoped ViewModel.
 * Refer to https://github.com/perettijuan/android_cool_coding/blob/master/ArchitectureComponents/app/src/main/java/com/jpp/architecturecomponents/ui/MainActivityViewModel.kt
 * for more information.
 * Example of CoroutineScope = https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html
 */
abstract class MPScopedViewModel : ViewModel(), CoroutineScope {

    private val currentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = currentJob + kotlinx.coroutines.Dispatchers.Main

    override fun onCleared() {
        currentJob.cancel()
        Log.d("ViewModel", "Scope is active $isActive")
        super.onCleared()
    }
}