package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.moviespreview.domainlayer.usecase.ConfigueApplicationUseCase
import com.jpp.moviespreview.domainlayer.usecase.ConfigureApplicationState
import com.jpp.moviespreview.screens.MPScopedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviesFragmentViewModel : MPScopedViewModel() {

    private val viewState by lazy { MutableLiveData<MoviesFragmentViewState>().apply { value = MoviesFragmentViewState.Loading } }
    private val useCase = ConfigueApplicationUseCase()

    fun bindViewState(): LiveData<MoviesFragmentViewState> = viewState

    fun onIntent(intent: MoviesFragmentIntent) {
        launchInScope {
            when (intent) {
                MoviesFragmentIntent.ConfigureApplication -> viewState.value = withContext(Dispatchers.Default) { configure() }
            }
        }
    }

    private fun configure(): MoviesFragmentViewState {
        return when (useCase.execute()) {
            ConfigureApplicationState.NoConnectivity -> MoviesFragmentViewState.ErrorNoConnectivity
            ConfigureApplicationState.Unknown -> MoviesFragmentViewState.ErrorUnknown
            ConfigureApplicationState.Success -> MoviesFragmentViewState.Configured
        }
    }
}