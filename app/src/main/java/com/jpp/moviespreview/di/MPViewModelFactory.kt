package com.jpp.moviespreview.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Custom ViewModelProvider.Factory implementation to be injected in Activities/Fragments.
 */
@Singleton
class MPViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T
}