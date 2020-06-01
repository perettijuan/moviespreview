package com.jpp.mp.common.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

/**
 * Convenient extension to set the value of the [MutableLiveData] that handles
 * [HandledEvent]s.
 */
fun <T> MutableLiveData<HandledEvent<T>>.setHandledEvent(event: T) {
    value = HandledEvent.of(event)
}

