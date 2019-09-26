package com.jpp.mp.common.coroutines

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.livedata.HandledEvent
import com.jpp.mp.common.livedata.HandledEvent.Companion.of
import com.jpp.mp.common.navigation.Destination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
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
 * Every time that this ViewModel is stopped (onCleared() is called), the CoroutineContext is killed, which
 * causes that any code being executed in the scope of the CoroutineContext is killed automatically.
 *
 * In order to facilitate unit testing of any subclass, the MPScopedViewModel defines a constructor that
 * receives an instance of [CoroutineDispatchers] that can be overridden in order to provide single-threaded
 * behavior.
 *
 * Example of CoroutineScope = https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html
 *
 *
 *
 * Navigation support:
 * The application uses the Android Navigation Architecture Components to perform the navigation between
 * different screens of the application. This provides a good number of benefits - all listed in
 * the Navigation Architecture web page - but it also comes with some difficulties.
 *
 * Updating the ActionBar title when a new section of the application is shown to the user is
 * one of the problems.
 * This ViewModel is part of the solution implemented to such problem: It provides a [LiveData]
 * with updates of [Destination]. Each time the VMs detects that the new section is being shown and
 * the UI is ready to render, the VM produces a new event that will be captured in order to update
 * the screen title.
 *
 * Another problem is related to how to navigate from a point to another when those points are in
 * different modules. In these cases, the origin does not knows anything about the new destination.
 * To such cases, this VM provides a mechanism to request a navigation to a new destination
 * each time it's needed.
 *
 * To see the full implementation of this solution, check [MPFragment] definition.
 */
abstract class MPScopedViewModel(val dispatchers: CoroutineDispatchers) : ViewModel(), CoroutineScope {

    /*
     * This Job represents the work that it is being done in the
     * background. It will be cancelled as soon as the ViewModel is
     * destroyed, meaning that any work being executed in the
     * context of the coroutine - the background - will be
     * automatically cancelled.
     */
    private val currentJob = SupervisorJob()

    /*
    * This is the scope in which the coroutines are executed. This indicates that, while the scope
    * is alive, any subclass of MPScopedViewModel can execute coroutines using the scope. When the scope
    * is killed, all work being executed in any ongoing coroutine is automatically cancelled.
    *
    * It is constructed using [MPScopedViewModel.currentJob] Job and the main dispatcher (UI dispatcher):
    *  - currentJob means that when the job is cancelled, the scope is killed.
    *  - the main dispatcher (UI dispatcher) means that any code that it is not specifically wrapped into another
    *    context, will execute in the UI thread, forcing us to be explicit when we're writing
    *    code that might take too long to finish.
    *
    * What is the plus (+) operator? -> https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines.experimental/-coroutine-context/plus.html
    */
    override val coroutineContext: CoroutineContext
        get() = currentJob + dispatchers.main()


    private val _destinationsEvent = MutableLiveData<Destination>()
    val destinationEvents: LiveData<Destination> get() = _destinationsEvent

    private val _navigationEvent = MutableLiveData<HandledEvent<Destination>>()
    val navigationEvents: LiveData<HandledEvent<Destination>> get() = _navigationEvent


    /**
     * Called when a [Destination] has been reached in the application. A call
     * to this method will produce a new event in [destinationEvents].
     */
    protected fun updateCurrentDestination(destination: Destination) {
        _destinationsEvent.value = destination
    }

    /**
     * Called when a new [destination] must be reached. This will post
     * a new event to [navigationEvents] in order to request a navigation
     * to the provided [destination].
     */
    protected fun navigateTo(destination: Destination) {
        _navigationEvent.value = of(destination)
    }


    override fun onCleared() {
        currentJob.cancel()
        Log.d("ViewModel", "Scope is active $isActive")
        super.onCleared()
    }
}