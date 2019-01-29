package com.jpp.moviespreview.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jpp.moviespreview.screens.main.MainActivityViewModel

@Suppress("UNCHECKED_CAST")
class TestMPViewModelFactory(private val viewModels: MutableMap<Class<out ViewModel>, ViewModel> = mutableMapOf(),
                             mainViewModel: MainActivityViewModel = MainActivityViewModel())
    : ViewModelProvider.Factory {

    init {
        viewModels[MainActivityViewModel::class.java] = mainViewModel
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = viewModels[modelClass] as T
    fun addVm(vm: ViewModel) {
        viewModels[vm.javaClass] = vm
    }
}