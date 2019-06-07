package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpmoviedetails.MovieDetailActionViewState.Closed
import com.jpp.mpmoviedetails.MovieDetailActionViewState.Open
import javax.inject.Inject

class MovieDetailsActionViewModel @Inject constructor(dispatchers: CoroutineDispatchers)
    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MutableLiveData<HandledViewState<MovieDetailActionViewState>>() }
    private var currentActionState: MovieDetailActionViewState = Closed

    fun onMainActionSelected() {
        pushActionState(when (currentActionState) {
            is Closed -> Open
            is Open -> Closed
        })
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<MovieDetailActionViewState>> get() = _viewStates

    private fun pushActionState(state: MovieDetailActionViewState) {
        currentActionState = state
        _viewStates.value = of(state)
    }
}