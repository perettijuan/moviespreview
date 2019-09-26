package com.jpp.mp.common.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.extensions.getViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mp.common.navigation.NavigationViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Base [Fragment] definition for all the fragments used in the application.
 */
abstract class MPFragment<VM : MPScopedViewModel> : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            destinationEvents.observe(viewLifecycleOwner, Observer {
                withNavigationViewModel { destinationReached(it) }
            })

            navigationEvents.observe(viewLifecycleOwner, Observer {
                withNavigationViewModel { it.actionIfNotHandled { navEvent -> navigateTo(navEvent) } }
            })
        }
    }

    private fun withNavigationViewModel(action: NavigationViewModel.() -> Unit) = getViewModel<NavigationViewModel>(viewModelFactory).action()

    /**
     * Updates the screen title of the application.
     */
    fun updateScreenTitle(screenTitle: String) {

    }

    /**
     * Updates the screen title of the application.
     */
    fun updateScreenTitle(screenTitle: Int) {
        updateScreenTitle(getString(screenTitle))
    }

    abstract fun withViewModel(action: VM.() -> Unit): VM
}