package com.jpp.mp.screens.main.header

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.R
import com.jpp.mp.common.extensions.getViewModel
import com.jpp.mp.common.extensions.withNavigationViewModel
import com.jpp.mp.databinding.FragmentNavHeaderBinding
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_nav_header.*
import javax.inject.Inject

/**
 * The application's UI shows a drawer navigation to allow the user redirect the app flow to different
 * places. This particular Fragment takes care of rendering the header of the drawer and allows the
 * user to login into the system or to be redirected to the account details.
 */
class NavigationHeaderFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewBinding: FragmentNavHeaderBinding

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_header, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navHeaderLoginButton.setOnClickListener {
            withViewModel { onNavigateToLoginSelected() }
        }

        navHeaderAccountDetailsTv.setOnClickListener {
            withViewModel { onNavigateToAccountDetailsSelected() }
        }

        withViewModel {
            viewStates.observe(this@NavigationHeaderFragment.viewLifecycleOwner, Observer { viewState -> viewBinding.viewState = viewState })
            navEvents.observe(this@NavigationHeaderFragment.viewLifecycleOwner, Observer { withNavigationViewModel(viewModelFactory) { navigateToUserAccount() } })
            onInit()
        }
    }

    private fun withViewModel(action: NavigationHeaderViewModel.() -> Unit) = getViewModel<NavigationHeaderViewModel>(viewModelFactory).action()
}