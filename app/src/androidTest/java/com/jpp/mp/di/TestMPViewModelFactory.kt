package com.jpp.mp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.main.MainActivityViewModel
import io.mockk.mockk

@Suppress("UNCHECKED_CAST")
class TestMPViewModelFactory(
    private val viewModels: MutableMap<Class<out ViewModel>, ViewModel> = mutableMapOf(),
    mainViewModel: MainActivityViewModel = MainActivityViewModel(mockk(relaxed = true))
) :
    ViewModelProvider.Factory {

    init {
        viewModels[MainActivityViewModel::class.java] = mainViewModel
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = viewModels[modelClass] as T
    fun addVm(vm: ViewModel) {
        viewModels[vm.javaClass] = vm
    }
}
