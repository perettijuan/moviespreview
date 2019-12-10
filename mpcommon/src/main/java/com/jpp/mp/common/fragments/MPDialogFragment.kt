package com.jpp.mp.common.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.extensions.getViewModel
import com.jpp.mp.common.navigation.NavigationViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Check [MPFragment] for a detailed explanation.
 */
abstract class MPDialogFragment<VM : MPScopedViewModel> : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
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

    /**
     * Helper function to access the [NavigationViewModel] of the Fragment.
     */
    private fun withNavigationViewModel(action: NavigationViewModel.() -> Unit) = getViewModel<NavigationViewModel>(viewModelFactory).action()

    /**
     * Generic function to perform actions with the [VM] that controls
     * this [MPFragment].
     */
    abstract fun withViewModel(action: VM.() -> Unit): VM
}
