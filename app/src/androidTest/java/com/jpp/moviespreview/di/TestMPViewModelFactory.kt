package com.jpp.moviespreview.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class TestMPViewModelFactory(private val viewModels: MutableMap<Class<out ViewModel>, ViewModel>) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = viewModels[modelClass] as T
    fun addVm(vm: ViewModel) {
        viewModels[vm.javaClass] = vm
    }
}