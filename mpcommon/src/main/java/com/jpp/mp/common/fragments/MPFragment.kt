package com.jpp.mp.common.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.extensions.withNavigationViewModel
import com.jpp.mp.common.navigation.Destination
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Base [Fragment] definition that contains some common functionality for all the fragments
 * implemented in the project.
 */
abstract class MPFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    /**
     * Updates the screen title of the application.
     */
    fun updateScreenTitle(screenTitle: String) {
        withNavigationViewModel(viewModelFactory) {
            destinationReached(Destination.ReachedDestination(screenTitle))
        }
    }

    /**
     * Updates the screen title of the application.
     */
    fun updateScreenTitle(screenTitle: Int) {
        updateScreenTitle(getString(screenTitle))
    }
}