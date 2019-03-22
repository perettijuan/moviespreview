package com.jpp.mp.screens.main.header

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.jpp.mp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_nav_header.*
import javax.inject.Inject

/**
 * Fragment that manages the header view shown in the Navigation Drawer in the MainActivity.
 */
class NavigationHeaderFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_nav_header, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navHeaderLoginButton.setOnClickListener {
            activity?.let {
                findNavController(it, R.id.mainNavHostFragment).navigate(R.id.accountFragment)
            }
        }
    }
}