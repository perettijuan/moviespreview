package com.jpp.moviespreview.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.isActive
import kotlin.coroutines.CoroutineContext

/**
 * Generic [ViewModel] to execute work that might cause long term interruptions in the UI thread.
 *
 * The mechanism used to execute background work and sync the state to the UI thread is Kotlin Coroutines.
 * In order to apply that mechanism, this [ViewModel] is a [CoroutineScope] to provide a structured enviroment
 * to execute coroutines - check https://medium.com/@elizarov/structured-concurrency-722d765aa952 to get more
 * information about structured coroutines.
 *
 * By default, all code executed in this class is executed in the Main Dispatcher (dispatchers.main()) - which is
 * equivalent to say that all the code in this class is executed in the UI thread.
 * In order to execute code that might block the UI thread, the dispatcher needs to be changed explicitly.
 * Example:
 *          withContext(dispatchers.default()) { longTermOperation() }
 *
 * Every time that this ViewModel is stopped (onCleared() os called), the CoroutineContext is killed, which
 * causes that any code being executed in the scope of the CoroutineContext is killed automatically.
 *
 * In order to facilitate unit testing of any subclass, the MPScopedViewModel defines a constructor that
 * receives an instance of [CoroutineDispatchers] that can be overridden in order to provide single-threaded
 * behavior.
 *
 * Example of CoroutineScope = https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html
 */
abstract class MPScopedViewModel(val dispatchers: CoroutineDispatchers) : ViewModel(), CoroutineScope {

    /*
     * This Job represents the work that it is being done in the
     * background. It will be cancelled as soon as the ViewModel is
     * destroyed, meaning that any work being executed in the
     * context of the coroutine - the background - will be
     * automatically cancelled.
     */
    private val currentJob = Job()

    /*
    * This is the scope in which the coroutines are executed. This indicates that, while the scope
    * is alive, any subclass of MPScopedViewModel can execute coroutines using the scope. When the scope
    * is killed, all work being executed in any ongoing coroutine is automatically cancelled.
    *
    * It is constructed using [MPScopedViewModel.currentJob] Job and the UI coroutine-context:
    *  - currentJob means that when the job is cancelled, the scope is killed.
    *  - the UI coroutine-context means that any code that it is not specifically wrapped into another
    *    context, will execute in the UI thread, forcing us to be explicit when we're writing
    *    code that might take too long to finish.
    *
    * What is the plus (+) operator? -> https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines.experimental/-coroutine-context/plus.html
    */
    override val coroutineContext: CoroutineContext
        get() = currentJob + dispatchers.main()

    override fun onCleared() {
        currentJob.cancel()
        Log.d("ViewModel", "Scope is active $isActive")
        super.onCleared()
    }
}