package com.jpp.mp.common.livedata

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Helps to avoid processing an event sent by LiveData when it is already being processed.
 * It is well known that LiveData attempts to deliver the last event pushed to any new
 * Observer that is attached to it. Some of these events - like view states - should be processed
 * only once. This wrapper fix that by ensuring that the value wrapped if processed only
 * once when accessed using [actionIfNotHandled].
 *
 * Source: https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
 */
class HandledEvent<out T>(private val viewState: T) {

    private var neverHandled = AtomicBoolean(true)

    /**
     * Execute the [action] provided if the view state wrapped has
     * not being processed yet.
     */
    fun actionIfNotHandled(action: (T) -> Unit) {
        if (neverHandled.compareAndSet(true, false)) {
            action(viewState)
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = viewState

    companion object {
        fun <T> of(viewState: T): HandledEvent<T> = HandledEvent(viewState)
    }
}
