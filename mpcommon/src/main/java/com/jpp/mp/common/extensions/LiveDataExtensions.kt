package com.jpp.mp.common.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.jpp.mp.common.livedata.HandledEvent

/**
 * Convenient extension to observer the values posted in a [LiveData].
 */
fun <T> LiveData<T>.observeValue(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer { newVal: T ->
        newVal?.let(observer)
    })
}

/**
 * Extension to observe and un-wrap the handled events.
 */
fun <T> LiveData<HandledEvent<T>>.observeHandledEvent(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer { handledEvent ->
        handledEvent.actionIfNotHandled(observer)
    })
}
