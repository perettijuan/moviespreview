package com.jpp.mpabout.licenses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.extensions.withNavigationViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpabout.R
import com.jpp.mpabout.databinding.FragmentLicensesBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class LicensesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewBinding: FragmentLicensesBinding


    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_licenses, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // sync app bar title
        withNavigationViewModel(viewModelFactory) { destinationReached(Destination.ReachedDestination(getString(R.string.about_open_source_action))) }
    }
}