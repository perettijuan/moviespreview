package com.jpp.mp.common.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.extensions.getViewModel
import com.jpp.mp.common.navigation.NavigationViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Base [Fragment] definition for all the fragments used in the application.
 *
 * [VM] represents the main [MPScopedViewModel] that the Fragment uses to provide the functionallity
 * requested to the user.
 *
 * Navigation support:
 * [MPFragment] works in conjunction with [MPScopedViewModel] to update the AppBar title each time
 * a new screen is shown. Basically, the [MPScopedViewModel] that is taking control of the application
 * posts a new event to [MPScopedViewModel.destinationEvents] in order to indicate that a new destination
 * has been reached. [MPFragment] captures that new state update and requests an update to the [NavigationViewModel]
 * that, in time, posts a new event to its [NavigationViewModel.reachedDestinations]. [MainActivity] captures
 * that new destination reached and updates the application's ActionBar with the title of the new destination.
 *
 *
 * Also, in order to provide navigation between the different modules in a decouple manner, [MPFragment]
 * works with [MPScopedViewModel] reacting to navigation updates posted in [MPScopedViewModel.navigationEvents].
 * Every time [MPScopedViewModel] posts a new destination to navigate to, [MPFragment] posts a new
 * event to [NavigationViewModel] that, in time, is processed by [MainActivity] to perform the navigation
 * using the Android Navigation Components.
 */
abstract class MPFragment<VM : MPScopedViewModel> : Fragment() {

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